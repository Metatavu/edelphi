/*global getLocale,S2,flatpickr,moment*/

var UserSubscriptionLevelEditorBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    
    this._planSelectChangeListener = this._onPlanSelectChange.bindAsEventListener(this);
    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
  },
  
  setup: function ($super) {
    $super($('panelAdminUserSubscriptionLevelEditorBlock'));

    this._setupFlatpicker("[name='subscription-started']");
    this._setupFlatpicker("[name='subscription-ends']", {
      "minDate": "today"
    });
    
    Event.observe($("plan-select"), "change", this._planSelectChangeListener);

    this._saveButton = $$('input[name="save"]')[0];
    Event.observe(this._saveButton, "click", this._saveButtonClickListener);
  },
  
  deinitialize: function ($super) {
    Event.stopObserving(this._saveButton, "click", this._saveButtonClickListener);
    Event.stopObserving($("plan-select"), "change", this._planSelectChangeListener);
  },
  
  _setupFlatpicker: function(element, options) {
    flatpickr(element, Object.extend({
      "locale": getLocale().getLanguage(),
      "altInput": true,
      "altFormat": "LL",
      "format": "ISO",
      "utc": true,
      "enableTime": false,
      "allowInput": false,
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
        if (format == "LL") {
          return moment(dateObj)
            .locale(getLocale().getLanguage())
            .format("LL");
        } else {
          return dateObj.getTime();
        }
      }
    }, options || {}));
  },
  
  _onPlanSelectChange: function (event) {
    var select = $("plan-select");
    var option = select.options[select.selectedIndex];
    var type = option.value;
    var started;
    var ends;
    
    switch (type) {
      case "CURRENT":
        started = moment(parseInt(option.readAttribute("data-started"))).toISOString();
        ends = moment(parseInt(option.readAttribute("data-ends"))).toISOString();
      break;
      case "BASIC":
        started = "";
        ends = "";
      break;
      default:
        var days = parseInt(option.readAttribute("data-days")) || 0;
        started = moment().startOf("day").toISOString();
        ends = moment().add(days, "days").endOf("day").toISOString();              
      break;
    }
    
    $$("[name='subscription-started']")[0]._flatpickr.setDate(started, true, "Z");
    $$("[name='subscription-ends']")[0]._flatpickr.setDate(ends, true, "Z");      
  },
  
  _onSaveButtonClick: function (event) {
    Event.stop(event);
    
    startLoadingOperation("admin.manageUserSubscriptionLevels.savingSubscriptionLevel");
    var formValues = $("adminUserSubscriptionLevelEditorForm").serialize(true);
    
    JSONUtils.request(CONTEXTPATH + "/admin/saveusersubscription.json", {
      parameters: formValues,
      onComplete: function (transport) {
        endLoadingOperation();
      },
      onSuccess: function (jsonResponse) {
        window.location.href = CONTEXTPATH + "/admin/manageusersubscription.page?user-id=" + formValues["user-id"];
      }
    });
  }
  
});

addBlockController(new UserSubscriptionLevelEditorBlockController());