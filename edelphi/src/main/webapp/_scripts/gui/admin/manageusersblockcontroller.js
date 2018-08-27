/*global getLocale,addBlockController,endLoadingOperation,JSONUtils,startLoadingOperation*/

var UsersEditorBlockController = Class.create(BlockController, {
  initialize: function ($super) {
    $super();
    
    this._emailInputKeyupListener = this._onEmailInputKeyUp.bindAsEventListener(this);
  },
  
  setup: function ($super) {
    $super($('adminManageUsersBlock'));
    
    this._emailInput = this.getBlockElement().down('input[name="email"]');
    Event.observe(this._emailInput, "keyup", this._emailInputKeyupListener);
  },
  
  deinitialize: function () {
    Event.stopObserving(this._emailInput, "keyup", this._emailInputKeyupListener);
  },
  
  _searchCallback: function (results) {
    if (results.length) {
      $("user-search-results").innerHTML = '';
      
      for (var i = 0; i < results.length; i++) {
        var result = results[i];
        var displayName = (result.firstName && result.lastName ? result.firstName + " " + result.lastName + " " : "") + "&lt;" + result.email + "&gt;";
        
        var userWrapper = new Element("div", { "class": "user-search-result" });
        var userName = new Element("div").update(displayName);
        var subscriptionLink = new Element("a", { href: "/admin/manageusersubscription.page?user-id=" + result.id }).update(getLocale().getText("admin.userSearch.manageUserSubscriptionLevel"));
        
        userWrapper.appendChild(userName);
        userWrapper.appendChild(subscriptionLink);
        
        $("user-search-results").appendChild(userWrapper);
      }
    } else {
      $("user-search-results").innerHTML = getLocale().getText("admin.userSearch.noResults");
    }
  },
  
  _onEmailInputKeyUp: function (event) {
    var value = event.target.value;
  
    $("user-search-results").addClassName("loading");
    
    JSONUtils.request(CONTEXTPATH + '/users/searchusers.json', {
      parameters: {
        text: value
      },
      onSuccess : function (jsonRequest) {
        this._searchCallback(jsonRequest.results);
        $("user-search-results").removeClassName("loading");
      }.bind(this)
    });
  }
  
});

addBlockController(new UsersEditorBlockController());