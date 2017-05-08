/*global BlockController,addBlockController,startLoadingOperation,endLoadingOperation.JSONUtils,transport */
(function() {
  'use strict';

  var MaterialImportGDocsBlockController = Class.create(BlockController, {
    initialize: function ($super) {
      $super();

      this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
    },
    setup: function ($super) {
      $super($('adminMaterialImportGDocsBlockContent'));
      
      this._formElement = this.getBlockElement().down('form[name="importGDocs"]');
      this._saveElement = this.getBlockElement().down("input[name='save']");
      Event.observe(this._saveElement, "click", this._saveButtonClickListener);
    },
    deinitialize: function ($super) {
      Event.stopObserving(this._saveElement, "click", this._saveButtonClickListener);
      $super();
    },
    _onSaveButtonClick: function (event) {
      Event.stop(event);
      
      startLoadingOperation("admin.block.importMaterialsGDocs.importingMaterials");
      JSONUtils.sendForm(this._formElement, {
        onComplete: function (transport) {
          endLoadingOperation();
        },
        onSuccess: function () {
          window.location.reload();
        }
      });
    }
  });

  addBlockController(new MaterialImportGDocsBlockController());
  
}).call(this);