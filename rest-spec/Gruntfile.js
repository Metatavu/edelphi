/*global module:false*/

const _ = require("lodash");
const fs = require("fs");
const rimraf = require("rimraf");

module.exports = function(grunt) {
  require("load-grunt-tasks")(grunt);
  
  const SWAGGER_VERSION = "3.0.5";
  const SWAGGER_JAR = `swagger-codegen-cli-${SWAGGER_VERSION}.jar`;
  const SWAGGER_URL = `https://search.maven.org/remotecontent?filepath=io/swagger/codegen/v3/swagger-codegen-cli/${SWAGGER_VERSION}/${SWAGGER_JAR}`;
  
  const JAXRS_ARTIFACT = "edelphi-api-spec";
  const JAXRS_PACKAGE = "fi.metatavu.edelphi.rest";
  const JAXRS_GROUP = "fi.metatavu.edelphi";
  const JAVA_ARTIFACT = "edelphi-api-client";
  const JAVA_PACKAGE = "fi.metatavu.edelphi.client";
  const JAVA_GROUP = "fi.metatavu.edelphi.client";
  const TYPESCRIPT_CLIENT_PACKAGE = "edelphi-client";
  const TYPESCRIPT_CLIENT_VERSION = require("./typescript-client-generated/package.json").version;

  grunt.registerMultiTask('typescript-process-any', 'Process any', function () {
    const modelFiles = {};
    const postfix = this.data.postfix;
    const field = this.data.field;
    const ignore = this.data.ignore || [];

    const objectFiles = fs.readdirSync(`${this.data.folder}/model`).filter((objectFile) => {
      return ignore.indexOf(objectFile) == -1  && objectFile.endsWith(`${postfix}.ts`);
    });

    objectFiles.forEach((objectFile) => {
      const modelName = objectFile.substring(0, 1).toUpperCase() + objectFile.substring(1, objectFile.length - 3);
      modelFiles[modelName] = objectFile.substring(0, objectFile.length - 3);
    });

    const objectModelType = Object.keys(modelFiles).join(" | ");
    
    const objectModelImports = Object.keys(modelFiles).map((modelName) => {
      const modelFile = modelFiles[modelName];
      return `import { ${modelName} } from './${modelFile}';`;
    }).join("\n");
    
    const eventFile = `${this.data.folder}/${this.data.file}`;
    let contents = fs.readFileSync(eventFile, "utf8");

    contents = contents.replace(/import \{ ModelObject } from \'\.\/modelObject\'\;/g, objectModelImports);
    contents = contents.replace(new RegExp(`${field}\\: ModelObject;`, 'g'), `${field}: ${objectModelType};`);
    contents = contents.replace(new RegExp(`${field}\\?\\: ModelObject;`, 'g'), `${field}?: ${objectModelType};`);

    fs.writeFileSync(eventFile, contents);
  });
  
  grunt.initConfig({
    "curl": {
      "swagger-codegen":  {
        src: SWAGGER_URL,
        dest: SWAGGER_JAR
      }
    },

    "clean": {
      "jaxrs-spec-cruft": [
        "jaxrs-spec-generated/src/main/java/fi/metatavu/edelphi/server/RestApplication.java"
      ],
      "jaxrs-spec-sources": ["jaxrs-spec-generated/src"],
      'java-sources': ['java-generated/src'],
      "typescript-client": [
        "typescript-client-generated/typings.json",
        "typescript-client-generated/api.module.ts",
        "typescript-client-generated/variables.ts",
        "typescript-client-generated/encoder.ts",
        "typescript-client-generated/configuration.ts"
      ]
    },
    "typescript-process-any": {
      "answer-data": {
        "folder": "typescript-client-generated",
        "postfix": "AnswerData",
        "file": "model/queryQuestionAnswer.ts",
        "field": "data"
      },
      "query-options": {
        "folder": "typescript-client-generated",
        "prefix": "QueryPage",
        "ignore": ["queryPageCommentOptions.ts", "reportRequestOptions.ts"],
        "file": "model/queryPage.ts",
        "field": "queryOptions"
      }
    },
    "shell": {
      "jaxrs-spec-generate": {
        command : "mv jaxrs-spec-generated/pom.xml jaxrs-spec-generated/pom.xml.before && " +
          `java -jar ${SWAGGER_JAR} generate ` +
          "-i ./swagger.yaml " +
          "-l jaxrs-spec " +
          `--api-package ${JAXRS_PACKAGE} ` +
          `--model-package ${JAXRS_PACKAGE}.model ` +
          `--group-id ${JAXRS_GROUP} ` +
          `--artifact-id ${JAXRS_ARTIFACT} ` +
          `--invoker-package ${JAXRS_PACKAGE} ` +
          "--template-engine handlebars " +
          "--artifact-version `cat jaxrs-spec-generated/pom.xml.before|grep version -m 1|sed -e \"s/.*<version>//\"|sed -e \"s/<.*//\"` " +
          "--template-dir jaxrs-spec-templates " +
          `--additional-properties dateLibrary=java8,useBeanValidation=true,sourceFolder=src/main/java,interfaceOnly=true,returnResponse=true,package=${JAXRS_PACKAGE},modelPackage=${JAXRS_PACKAGE}.model ` +
          "-o jaxrs-spec-generated/"
      },
      "jaxrs-fix-folders": {
        command : "mkdir -p jaxrs-spec-generated/src/main/java/fi/metatavu/edelphi/rest && mv jaxrs-spec-generated/src/main/java/io/swagger/* jaxrs-spec-generated/src/main/java/fi/metatavu/edelphi/rest"
      },
      "jaxrs-spec-install": {
        command : "mvn install",
        options: {
          execOptions: {
            cwd: "jaxrs-spec-generated"
          }
        }
      },
      "jaxrs-spec-release": {
        command : "git add src pom.xml swagger.json && git commit -m 'Generated source' && git push && mvn -B release:clean release:prepare release:perform",
        options: {
          execOptions: {
            cwd: "jaxrs-spec-generated"
          }
        }
      },
      'java-generate': {
        command : 'mv java-generated/pom.xml java-generated/pom.xml.before && ' +
          `java -jar ${SWAGGER_JAR} generate ` +
          '-i ./swagger.yaml ' +
          '-l java ' +
          `--api-package ${JAVA_PACKAGE} ` +
          `--model-package ${JAVA_PACKAGE}.model ` +
          `--group-id ${JAVA_GROUP} ` +
          `--artifact-id ${JAVA_ARTIFACT} ` +
          '--artifact-version `cat java-generated/pom.xml.before|grep version -m 1|sed -e \'s/.*<version>//\'|sed -e \'s/<.*//\'` ' +
          "--template-engine handlebars " +
          '--template-dir java-templates ' +
          '--additional-properties library=feign,dateLibrary=java8,sourceFolder=src/main/java,supportingFiles=true ' +
          '-o java-generated/'
      },
      'java-install': {
        command : 'mvn install',
        options: {
          execOptions: {
            cwd: 'java-generated'
          }
        }
      },
      'java-release': {
        command : 'git add src pom.xml && git commit -m "Generated source" && git push && mvn -B release:clean release:prepare release:perform',
        options: {
          execOptions: {
            cwd: 'java-generated'
          }
        }
      },
      "typescript-client-generate": {
        command : `java -jar ${SWAGGER_JAR} generate ` +
          "-i ./swagger.yaml " +
          "-l typescript-angular " +
          "-t typescript-client-template/ " +
          "-o typescript-client-generated/ " +
          "--template-engine mustache " +
          "--type-mappings Date=string " +
          `--additional-properties projectName=${TYPESCRIPT_CLIENT_PACKAGE},npmName=${TYPESCRIPT_CLIENT_PACKAGE},npmVersion=${TYPESCRIPT_CLIENT_VERSION}`
      },
      'typescript-client-bump-version': {
        command: 'npm version patch',
        options: {
          execOptions: {
            cwd: 'typescript-client-generated'
          }
        }
      },
      'typescript-client-push': {
        command : 'git add . && git commit -m "Generated javascript source" && git push',
        options: {
          execOptions: {
            cwd: 'typescript-client-generated'
          }
        }
      },
      "typescript-client-publish": {
        command : 'npm install && npm run build && npm publish',
        options: {
          execOptions: {
            cwd: 'typescript-client-generated'
          }
        }
      },
    }
  });

  grunt.registerTask("download-dependencies", "if-missing:curl:swagger-codegen");
  grunt.registerTask("jaxrs-gen", [ "download-dependencies", "clean:jaxrs-spec-sources", "shell:jaxrs-spec-generate", "shell:jaxrs-fix-folders", "clean:jaxrs-spec-cruft", "shell:jaxrs-spec-install" ]);
  grunt.registerTask("jaxrs-spec", [ "jaxrs-gen", "shell:jaxrs-spec-release" ]);
  grunt.registerTask('java-gen', [ 'download-dependencies', 'clean:java-sources', 'shell:java-generate', 'shell:java-install' ]);
  grunt.registerTask('java', [ 'java-gen', 'shell:java-release' ]);

  grunt.registerTask('typescript-client-gen', [ 'download-dependencies', 'shell:typescript-client-generate', 'typescript-process-any:query-options', 'typescript-process-any:answer-data', 'clean:typescript-client']);
  grunt.registerTask('typescript-client', [ 'typescript-client-gen', "shell:typescript-client-bump-version", "shell:typescript-client-push", "shell:typescript-client-publish" ]);

  grunt.registerTask("default", [ "jaxrs-spec", "java"]);
  
};
