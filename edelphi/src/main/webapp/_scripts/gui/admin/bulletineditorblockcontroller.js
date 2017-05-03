/*global getLocale,S2,flatpickr,moment*/
BulletinEditorBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    
    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('panelAdminBulletinEditorBlock'));
    
//    var panelId = JSDATA['securityContextId'];

    this._ckEditor = CKEDITOR.replace('message', {
// TODO: Can we load all extra plugins from same config file or should we load plugins based on individual editor instances?    
//      extraPlugins: 'fnigenericbrowser',
      toolbar: "materialToolbar",
      fniGenericBrowser:{
        enabledInDialogs: ['image', 'link'],
        connectorUrl: CONTEXTPATH + '/system/ckbrowserconnector.json'
      }
    });
    
    flatpickr('input[name="importantEnds"]', {
      "locale": getLocale().getLanguage(),
      "altInput": true,
      "altFormat": "LLL",
      "format": "ISO",
      "utc": true,
      "enableTime": true,
      "time_24hr": true,
      "allowInput": false,
      "minDate": "today",
      "parseDate": function (value) {
        if (value) {
          var intValue = parseInt(value);
          if (intValue) {
            return new Date(intValue);
          }
        }
        
        return null;
      },
      "formatDate": function (dateObj, format) {
        if (format == 'LLL') {
          return moment(dateObj)
            .locale(getLocale().getLanguage())
            .format('LLL');
        } else {
          return dateObj.getTime();
        }
      }
    });
    
    this._saveButton = this.getBlockElement().down('input[name="save"]');
    Event.observe(this._saveButton, "click", this._saveButtonClickListener);
    this._bulletinId = this.getBlockElement().down('input[name="bulletinId"]').value;
  },
  deinitialize: function ($super) {
    Event.stopObserving(this._saveButton, "click", this._saveButtonClickListener);
    this._ckEditor.destroy();
    
    $super();
  },
  _onSaveButtonClick: function (event) {
    Event.stop(event);
    
    var title = this.getBlockElement().down('input[name="title"]').value;
    var message = this._ckEditor.getData();
    if (message) {
      var important = this.getBlockElement().down('input[name="important"]:checked') ? 'TRUE' : 'FALSE';
      var importantEnds = this.getBlockElement().down('input[name="importantEnds"]').value;
      
      startLoadingOperation("panelAdmin.block.bulletins.savingBulletin");
      
      if (this._bulletinId) {
        JSONUtils.request(CONTEXTPATH + '/admin/updatebulletin.json', {
          parameters: {
            bulletinId: this._bulletinId,
            title: title,
            message: message,
            important: important,
            importantEnds: importantEnds
          },
          onComplete: function (transport) {
            endLoadingOperation();
          },
          onSuccess: function (jsonResponse) {
            window.location.href = CONTEXTPATH + '/admin/editbulletin.page?bulletinId=' + jsonResponse.bulletinId;
          }
        });
      } else {
        JSONUtils.request(CONTEXTPATH + '/admin/createbulletin.json', {
          parameters: {
            title: title,
            message: message,
            important: important,
            importantEnds: importantEnds
          },
          onComplete: function (transport) {
            endLoadingOperation();
          },
          onSuccess: function (jsonResponse) {
            window.location.href = CONTEXTPATH + '/admin/editbulletin.page?bulletinId=' + jsonResponse.bulletinId;
          }
        });
      }
    }
    else {
      var eventQueue = getGlobalEventQueue();
      eventQueue.addItem(new EventQueueItem(getLocale().getText("panelAdmin.block.bulletins.emptyContent"), {
        className: "eventQueueWarningItem",
        timeout: -1
      }));
      
    }
  }
});

addBlockController(new BulletinEditorBlockController());