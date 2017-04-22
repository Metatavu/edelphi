/*global CONTEXTPATH,BlockController,JSONUtils,addBlockController,startLoadingOperation,endLoadingOperation */
(function() {
  'use strict';
  
  var ChangePlanBlockController = Class.create(BlockController, {
    initialize: function ($super) {
      $super();
      
      this._changeButtonClickListener = this._onChangeButtonClick.bindAsEventListener(this);
    },
    setup: function ($super) {
      $super($('changePlanGenericBlockContent'));
      this._changeButton = this.getBlockElement().down('input[name="change"]');
      Event.observe(this._changeButton, "click", this._changeButtonClickListener);
      
      var radioButtons = this.getBlockElement().select('*[name="planId"]:not(:disabled)');
      if (radioButtons.length) {
        radioButtons[0].setAttribute("checked", "checked");
      } else {
        this._changeButton.setAttribute("disabled", "disabled");
      }
    },
    deinitialize: function ($super) {
      Event.stopObserving(this._changeButton, "click", this._changeButtonClickListener);
      $super();
    },
    _onChangeButtonClick: function (event) {
      Event.stop(event);
      var formData = this.getBlockElement().down('form').serialize(true);
      var planId = formData['planId'];
      window.location.href = '/orderplan.page?planId=' + planId;
    }
  });

  addBlockController(new ChangePlanBlockController());
  
}).call(this);