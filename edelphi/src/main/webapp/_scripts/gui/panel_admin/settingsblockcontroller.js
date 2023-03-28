/**
 * Trigger event from legacy code into ReactJs
 *
 * @param string command
 * @param object event data
 */
function triggerReactCommand(command, data) {
  var event = new CustomEvent('react-command', {
    bubbles: true,
    detail: {
      command: command,
      data: data || {}
    }
  });

  document.dispatchEvent(event);
}

SettingsBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
  },
  setup: function ($super) {
    $super($('panelAdminSettingsBlockContent'));
    this._formElement = $('panelAdminSettingsForm');
    this._saveButton = this._formElement.down("input[name='save']");
    this._deleteButton = $("dashboard-delete-panel");
    Event.observe(this._saveButton, "click", this._saveButtonClickListener);

    if (this._deleteButton) {
      Event.observe(this._deleteButton, "click", this._deleteButtonClickListener);
    }
  },
  deinitialize: function ($super) {
    Event.stopObserving(this._saveButton, "click", this._saveButtonClickListener);

    if (this._deleteButton) {
      Event.stopObserving(this._deleteButton, "click", this._deleteButtonClickListener);
    }
  },
  _onSaveButtonClick: function (event) {
    Event.stop(event);
    JSONUtils.sendForm(this._formElement, {
      onSuccess: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
      },
      onFailure: function (jsonResponse) {
        JSONUtils.showMessages(jsonResponse);
      }
    });
  },
  _deleteButtonClickListener: function (event) {
    triggerReactCommand("open-delete-panel-dialog", {});
  }
});

addBlockController(new SettingsBlockController());