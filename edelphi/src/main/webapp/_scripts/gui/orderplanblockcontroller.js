/*global CONTEXTPATH,BlockController,JSONUtils,addBlockController,startLoadingOperation,endLoadingOperation */
(function() {
  'use strict';
  
  var PlanEditorBlockController = Class.create(BlockController, {
    initialize: function ($super) {
      $super();
      
      this._orderButtonClickListener = this._onOrderButtonClick.bindAsEventListener(this);
    },
    setup: function ($super) {
      $super($('orderPlanDetailsBlockContent'));
      this._orderButton = this.getBlockElement().down('input[name="order"]');
      Event.observe(this._orderButton, "click", this._orderButtonClickListener);
    },
    deinitialize: function ($super) {
      Event.stopObserving(this._orderButton, "click", this._orderButtonClickListener);
      $super();
    },
    _onOrderButtonClick: function (event) {
      startLoadingOperation("orderplan.block.preparingOrder");
    }
  });

  addBlockController(new PlanEditorBlockController());
  
}).call(this);