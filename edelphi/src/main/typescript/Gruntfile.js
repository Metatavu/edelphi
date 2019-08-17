module.exports = function(grunt) {

  require('load-grunt-tasks')(grunt);
  
  grunt.initConfig({
    "shell": {
      "webpack": {
        "command": "node node/node_modules/npm/bin/npx-cli.js webpack",
        "options": {
          "execOptions": {
            cwd: "."
          }
        }
      }
    }
  });
  
  grunt.registerTask('default', [
    "shell:webpack"
  ]);
  
};