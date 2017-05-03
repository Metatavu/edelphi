/*global getLocale,S2*/
(function() {
  'use strict';
  
  document.observe("dom:loaded", function() {

    new Ajax.Request('/getimportantbulletins.json', {
      onSuccess : function(responseObject) {
        var content = new Element("div", { });
        var response = responseObject.responseText.evalJSON();
        
        for (var i = 0; i < response.results.length; i++) {
          var bulletin = response.results[i];
          var bulletinElement = new Element("div", { className: "bulletin" });
          var published = getLocale().getText("bulletins.important.bulletinPublished", [ getLocale().getDateTime(bulletin.created) ]);

          bulletinElement.appendChild(new Element("h3", { className: "bulletin-title" }).update(bulletin.title));
          bulletinElement.appendChild(new Element("div", { className: "bulletin-date" }).update(published));
          bulletinElement.appendChild(new Element("div", { className: "bulletin-message" }).update(bulletin.message));
          
          content.appendChild(bulletinElement);
        }
        
        var dialog = new S2.UI.Dialog({
          zIndex: 2000,
          modal: true,
          content: content,
          buttons: [{
            label: getLocale().getText("bulletins.important.dialogOkButton"),
            action: function(instance) {
              instance.close(false);
            }
          }]
        });

        dialog.toElement().addClassName("important-bulletin-dialog");
        
        dialog.open();
      }
    });
  });
  
}).call(this);