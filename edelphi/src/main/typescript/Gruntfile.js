module.exports = function(grunt) {

  require('load-grunt-tasks')(grunt);
  
  grunt.initConfig({
    "shell": {
      "webpack": {
        "command": "npx webpack",
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