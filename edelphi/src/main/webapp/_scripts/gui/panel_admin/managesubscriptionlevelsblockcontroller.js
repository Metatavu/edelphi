/*global CONTEXTPATH,BlockController,JSONUtils,addBlockController,startLoadingOperation,endLoadingOperation */
(function() {
  'use strict';
  
  var SubscriptionLevelsEditorBlockController = Class.create(BlockController, {
    initialize: function ($super) {
      $super();
      
      this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
    },
    setup: function ($super) {
      $super($('panelAdminSubscriptionLevelsEditorBlock'));
      this._saveButton = this.getBlockElement().down('input[name="save"]');
      Event.observe(this._saveButton, "click", this._saveButtonClickListener);
    },
    deinitialize: function ($super) {
      Event.stopObserving(this._saveButton, "click", this._saveButtonClickListener);
      $super();
    },
    _onSaveButtonClick: function (event) {
      Event.stop(event);
      
      var formData = this.getBlockElement().down('form').serialize(true);
      startLoadingOperation("panelAdmin.block.subscriptionlevels.saving");
      
      JSONUtils.request(CONTEXTPATH + '/admin/managesubscriptionlevels.json', {
        parameters: formData,
        onComplete: function () {
          endLoadingOperation();
        },
        onSuccess: function () {
          window.location.href = CONTEXTPATH + '/admin/managesubscriptionlevels.page';
        }
      });
      
    }
  });

  addBlockController(new SubscriptionLevelsEditorBlockController());
  
}).call(this);