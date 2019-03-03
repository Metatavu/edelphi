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
  const TYPESCRIPT_MODEL_PACKAGE = "edelphi-typescript-models";
  const TYPESCRIPT_MODEL_VERSION = require("./typescript-model-generated/package.json").version;

  grunt.registerMultiTask('typescript-model-post-process', 'Post process', function () {
    const modelFiles = {};

    const eventDataFiles = fs.readdirSync(`${this.data.folder}/model`).filter((eventDataFile) => {
      return eventDataFile.endsWith("EventData.ts");
    });

    eventDataFiles.forEach((eventDataFile) => {
      const modelName = eventDataFile.substring(0, 1).toUpperCase() + eventDataFile.substring(1, eventDataFile.length - 3);
      modelFiles[modelName] = eventDataFile.substring(0, eventDataFile.length - 3);
    });

    const eventDataModelType = Object.keys(modelFiles).join(" | ");

    const eventDataModelImports = Object.keys(modelFiles).map((modelName) => {
      const modelFile = modelFiles[modelName];
      return `import { ${modelName} } from './${modelFile}';`;
    }).join("\n");

    const eventFile = `${this.data.folder}/model/event.ts`;
    let contents = fs.readFileSync(eventFile, "utf8");
    contents = contents.replace(/import \{ ModelObject } from \'\.\/modelObject\'\;/g, eventDataModelImports);
    contents = contents.replace(/data\: ModelObject\;/g, `data: ${eventDataModelType};`);
    contents = contents.replace(/data\?\: ModelObject\;/g, `data?: ${eventDataModelType};`);

    fs.writeFileSync(eventFile, contents);
  });

  grunt.registerMultiTask('typescript-api-post-process', 'Post process', function () {
    const serviceFiles = fs.readdirSync(`${this.data.folder}/api`).filter((file) => {
      return file.endsWith("service.ts");
    })  
    .map((file) => {
      return `${this.data.folder}/api/${file}`;
    });

    serviceFiles.forEach((serviceFile) => {
      let contents = fs.readFileSync(serviceFile, "utf8");
      contents = contents.replace(/from '\.\.\/model\/[a-zA-Z]*\'/g, 'from "edelphi-typescript-models"');
      fs.writeFileSync(serviceFile, contents);
    });

    fs.renameSync(`${this.data.folder}/api/api.ts`, `${this.data.folder}/api/index.ts`);

    if (process.env.EDELPHI_API_FOLDER) {
      if (fs.existsSync(process.env.EDELPHI_API_FOLDER)) {
        rimraf.sync(process.env.EDELPHI_API_FOLDER);
      }

      fs.renameSync(`${this.data.folder}/api`, process.env.EDELPHI_API_FOLDER);
    }
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
      "typescript-model-api": [
        "typescript-model-generated/api", 
        "typescript-model-generated/api.module.ts", 
        "typescript-model-generated/typings.json",
        "typescript-model-generated/variables.ts",
        "typescript-model-generated/encoder.ts",
        "typescript-model-generated/configuration.ts",
        "typescript-model-generated/git_push.sh"
      ]
    },
    "typescript-model-post-process": {
      "event-data": {
        "folder": "typescript-model-generated"
      }
    },
    "typescript-api-post-process": {
      "api": {
        "folder": "typescript-model-generated"
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
      "typescript-model-generate": {
        command : `java -jar ${SWAGGER_JAR} generate ` +
          "-i ./swagger.yaml " +
          "-l typescript-angular " +
          "-t typescript-model-template/ " +
          "-o typescript-model-generated/ " +
          "--template-engine mustache " +
          "--type-mappings Date=string " +
          `--additional-properties projectName=${TYPESCRIPT_MODEL_PACKAGE},npmName=${TYPESCRIPT_MODEL_PACKAGE},npmVersion=${TYPESCRIPT_MODEL_VERSION}`
      },
      'typescript-model-bump-version': {
        command: 'npm version patch',
        options: {
          execOptions: {
            cwd: 'typescript-model-generated'
          }
        }
      },
      'typescript-model-push': {
        command : 'git add . && git commit -m "Generated javascript source" && git push',
        options: {
          execOptions: {
            cwd: 'typescript-model-generated'
          }
        }
      },
      "typescript-model-publish": {
        command : 'npm publish',
        options: {
          execOptions: {
            cwd: 'typescript-model-generated'
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
  grunt.registerTask('typescript-model-gen', [ 'shell:typescript-model-generate', 'clean:typescript-model-api', 'typescript-model-post-process:event-data' ]);
  grunt.registerTask('typescript-model', [ 'typescript-model-gen', "shell:typescript-model-bump-version", "shell:typescript-model-push", "shell:typescript-model-publish" ]);
  grunt.registerTask('typescript-api-gen', [ 'shell:typescript-model-generate', 'typescript-api-post-process:api' ]);

  grunt.registerTask("default", [ "jaxrs-spec", "java"]);
  
};
