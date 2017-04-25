/*global CONTEXTPATH,BlockController,JSONUtils,addBlockController,startLoadingOperation,endLoadingOperation */
(function() {
  'use strict';
  
  var PlanEditorBlockController = Class.create(BlockController, {
    initialize: function ($super) {
      $super();
      
      this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
    },
    setup: function ($super) {
      $super($('panelAdminPlanEditorBlock'));
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
      startLoadingOperation("panelAdmin.block.planEditor.saving");
      
      JSONUtils.request(CONTEXTPATH + '/admin/manageplan.json', {
        parameters: formData,
        onComplete: function () {
          endLoadingOperation();
        },
        onSuccess: function () {
          window.location.href = CONTEXTPATH + '/admin/manageplan.page?planId=' + formData['planId'];
        }
      });
      
    }
  });

  addBlockController(new PlanEditorBlockController());
  
}).call(this);