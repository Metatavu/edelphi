CKEDITOR.plugins.add('disablehttplink', {
  onLoad: function () {
    CKEDITOR.on('dialogDefinition', function (ev) {
      var dialogName = ev.data.name;
      var dialogDefinition = ev.data.definition;
      if (dialogName == 'link') {
        var info = dialogDefinition.getContents('info');
        var protocol = info.get('protocol');
        var originalItems = protocol['items'];

        var newItems = originalItems.filter(function (item) {
          return item[0] != 'http://' && item[1] != 'http://';
        });

        protocol['items'] = newItems;
        protocol['default'] = 'https://';
      }
    });
  },
  requires: ['dialog']
});