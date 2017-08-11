/*globals getGlobalEventQueue,EventQueueItem,getLocale*/

var InviteActionsBlockController = Class.create(BlockController, {
  initialize: function($super) {
    $super();
    this._inviteFieldChangeListener = this._onInviteFieldChange.bindAsEventListener(this);
    this._inviteFieldClickListener = this._onInviteFieldClick.bindAsEventListener(this);
    this._inviteFieldEnterListener = this._onInviteFieldEnter.bindAsEventListener(this);
    this._validEmailMask = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
    this._hasInviteFieldHelp = true;
    this._lastSearchValue = null;
    this._csvButtonClickListener = this._onCSVButtonClick.bindAsEventListener(this);
    this._csvLoadedListener = this._onCSVLoaded.bindAsEventListener(this);
    this._saveButtonClickListener = this._onSaveButtonClick.bindAsEventListener(this);
    this._resendInvitationsListener = this._onResendInvitations.bindAsEventListener(this);
  },
  setup: function($super) {
    $super($('panelAdminInviteUsersActionsBlockContent'));
    this._formElement = $('panelAdminInvitationForm');
    this._autoCompleteList = new AutoCompleteList(this);
    this._invitationList = new InvitationList(this, $('inviteUsersSelectedInvitationUsers'));
    this._inviteField = this._formElement.inviteUser;
    this._inviteField.parentNode.insertBefore(this._autoCompleteList.getGuiElement(), this._inviteField.nextSibling);
    this._csvButton = this._formElement.csvButton;
    this._saveButton = this._formElement.sendInvitations;
    Event.observe(this._inviteField, "click", this._inviteFieldClickListener);
    Event.observe(this._inviteField, "keydown", this._inviteFieldEnterListener);
    Event.observe(this._inviteField, "keyup", this._inviteFieldChangeListener);
    Event.observe(this._saveButton, "click", this._saveButtonClickListener);
    Event.observe(this._csvButton, "click", this._csvButtonClickListener);
    Event.observe($('csvFileContent'), "load", this._csvLoadedListener);
    Event.observe(document, "ed:resendInvitation", this._resendInvitationsListener);
  },
  deinitialize: function($super) {
    $super();
    Event.stopObserving(this._inviteField, "click", this._inviteFieldClickListener);
    Event.stopObserving(this._inviteField, "keydown", this._inviteFieldEnterListener);
    Event.stopObserving(this._inviteField, "keyup", this._inviteFieldChangeListener);
    Event.stopObserving(this._saveButton, "click", this._saveButtonClickListener);
    Event.stopObserving(this._csvButton, "click", this._csvButtonClickListener);
    Event.stopObserving($('csvFileContent'), "load", this._csvLoadedListener);
    Event.stopObserving(document, "ed:resendInvitation", this._resendInvitationsListener);
  },
  autoCompleteEntryClicked: function (entry) {
    if (!this._invitationList.contains(entry.getId(), entry.getEmail())) {
      this._invitationList.add(entry.getId(), entry.getFirstName(), entry.getLastName(), entry.getEmail());
    }
    this._autoCompleteList.clear();
    this._autoCompleteList.setVisible(false);
  },
  invitationEntryClicked: function (entry) {
    this._invitationList.remove(entry);
  },
  _showSearchResults: function(results) {
    this._autoCompleteList.clear();
    if (results.length == 0) {
      var email = this._inviteField.value;
      if (this._validEmailMask.test(email) && !this._invitationList.contains(null, email)) {
        this._autoCompleteList.add(null, null, null, email);
      }
    }
    else {
      for (var i = 0, l = results.length; i < l; i++) {
        if (!this._invitationList.contains(results[i].id, results[i].email)) {
          this._autoCompleteList.add(results[i].id, results[i].firstName, results[i].lastName, results[i].email);
        }
      }
    }
    this._autoCompleteList.setVisible(this._autoCompleteList.getSize() > 0);
  },
  
  _getResponseError: function (response) {
    if (response && response.messages && response.messages.length) {
      for (var i = 0; i < response.messages.length; i++) {
        if (response.messages[i].severity !== "OK") {
          return response.messages[i].message;
        }
      }
    }
    
    return null;
  },

  _createUserAddRequest: function (userId, queryId, email, firstName, lastName, password, progressCallback) {
    return function (callback) {
      JSONUtils.request(CONTEXTPATH + '/panel/admin/adduser.json', {
        parameters: {
          email: email,
          userId: userId,
          queryId: queryId,
          firstName: firstName,
          lastName: lastName,
          password: password
        },
        onSuccess : function(response) {
          var error = this._getResponseError(response);
          progressCallback(error);
          callback(null, {
            error: error,
            email: email
          });
        }.bind(this),
        onFailure: function (response) {
          var error = this._getResponseError(response);
          progressCallback(error);
          callback(null, {
            error: error,
            email: email
          });
        }.bind(this)
      });  
    }.bind(this)
  },
  
  _createInvitationRequest: function (invitationMessage, userId, queryId, email, progressCallback) {
    return function (callback) {
      JSONUtils.request(CONTEXTPATH + '/panel/admin/createinvitation.json', {
        parameters: {
          invitationMessage: invitationMessage,
          email: email,
          userId: userId,
          queryId: queryId
        },
        onSuccess : function(response) {
          var error = this._getResponseError(response);
          progressCallback(error);
          callback(null, {
            error: error,
            email: email
          });
        }.bind(this),
        onFailure: function (response) {
          var error = this._getResponseError(response);
          progressCallback(error);
          callback(null, {
            error: error,
            email: email
          });
        }.bind(this)
      });  
    }.bind(this)
  },
  
  _showMessage: function (severity, message, timeout) {
    var eventQueue = getGlobalEventQueue();
    
    switch (severity) {
      case 'OK':
        eventQueue.addItem(new EventQueueItem(message, {
          className: "eventQueueSuccessItem",
          timeout: timeout || 1000 * 2
        }));
      break;
      case 'INFORMATION':
        eventQueue.addItem(new EventQueueItem(message, {
          className: "eventQueueInfoItem",
          timeout: timeout || 1000 * 30
        }));
      break;
      case 'WARNING':
        eventQueue.addItem(new EventQueueItem(message, {
          className: "eventQueueWarningItem",
          timeout: timeout || -1
        }));          
      break;
      case 'ERROR':
        eventQueue.addItem(new EventQueueItem(message, {
          className: "eventQueueErrorItem",
          timeout: timeout || -1
        }));
      break;
      case 'CRITICAL':
        eventQueue.addItem(new EventQueueItem(message, {
          className: "eventQueueCriticalItem",
          timeout: timeout || -1
        }));
      break;
      default:
        eventQueue.addItem(new EventQueueItem(message, {
          className: "eventQueueErrorItem",
          timeout: timeout || -1
        }));
      break;
    }
  },

  _showSuccess: function (message, timeout) {
    this._showMessage('OK', message, timeout);
  },
  
  _showError: function (message, timeout) {
    this._showMessage('ERROR', message, timeout);
  },
  
  _onInviteFieldEnter: function (event) {
    if (event.keyCode == 13) {
      Event.stop(event);
      if (this._autoCompleteList.getSize() == 1) {
        this.autoCompleteEntryClicked(this._autoCompleteList.get(0));
        this._inviteField.select();
      }
    }
  },
  _onInviteFieldChange: function () {
    var value = this._inviteField.value.strip();
    if (value && value != '') {
      if (value !== this._lastSearchValue || !this._autoCompleteList.isVisible()) {
        var parameters = {
          text: value
        };
        var _this = this;
        JSONUtils.request(CONTEXTPATH + '/users/searchusers.json', {
          parameters: parameters,
          onSuccess : function(jsonRequest) {
            _this._lastSearchValue = value;
            _this._showSearchResults(jsonRequest.results);
          }
        });
      }
    }
    else {
      this._autoCompleteList.clear();
      this._autoCompleteList.setVisible(false);
    }
  },
  
  _onInviteFieldClick: function() {
    if (this._hasInviteFieldHelp === true) {
      this._inviteField.value = '';
      this._hasInviteFieldHelp = false;
    }
    else if (this._inviteField.value != '') {
      this._onInviteFieldChange();
    }
    this._inviteField.select();
  },
  
  _addUsers: function () {
    var loadItem = startLoadingOperation("panelAdmin.block.invitations.addUsers");
    
    var userCount = this._invitationList.getSize();
    var addFailCount = 0;
    var addSuccessCount = 0;
    var queryId = $(this._formElement.queryId).getValue();
    var password = (Math.random() + 0.1).toString(36).slice(2).substring(0, 8);
    
    var addRequests = [];
    var progressCallback = function(err) {
      if (err && "USER_EXISTS" !== err) {
        addFailCount++;
      } else {
        addSuccessCount++;
      }
      
      var countText = addSuccessCount;
      if (addFailCount > 0) {
        countText += ' ' + getLocale().getText("panelAdmin.block.invitations.addingUsersFailed", addFailCount);
      }
      
      var text = getLocale().getText("panelAdmin.block.invitations.addingUsers", countText, userCount);
      loadItem.updateText(text);
    };
    
    for (var i = 0; i < userCount; i++) {
      var userId = $(this._formElement['inviteUser.' + i + '.id']).getValue();
      var email = $(this._formElement['inviteUser.' + i + '.email']).getValue();
      var firstName = $(this._formElement['inviteUser.' + i + '.firstName']).getValue();
      var lastName = $(this._formElement['inviteUser.' + i + '.lastName']).getValue();
      addRequests.push(this._createUserAddRequest(userId, queryId, email, firstName, lastName, password, progressCallback));
    }
    
    async.parallelLimit(addRequests, 2, function (err, results) {
      if (err) {
        this._showError(err);
      } else {
        var errorMails = [];
        var successCount = 0;
        
        for (var i = 0; i < results.length; i++) {
          var result = results[i];
          if (result && result.error) {
            if (result.error !== 'USER_EXISTS') {
              errorMails.push(result.email);
            }
          } else {
            successCount++;
          }
        }
        
        if (errorMails.length) {
          this._showError(getLocale().getText('panelAdmin.block.invitations.invitationFailures', errorMails.join(',')));
        }
        
        this._showSuccess(getLocale().getText("panelAdmin.block.invitations.passwordGenerated", successCount, password), -1);
        
        document.fire("ed:invitationListRefreshRequired");
        this._invitationList.clear();
      }
      
      endLoadingOperation();
    }.bind(this));
  },
  
  _sendInvitations: function () {
    var loadItem = startLoadingOperation("panelAdmin.block.invitations.creatingInvitations");
    
    var invitationCount = this._invitationList.getSize();
    var addFailCount = 0;
    var addSuccessCount = 0;
    var queryId = $(this._formElement.queryId).getValue();
    
    var addRequests = [];
    var invitationMessage = $(this._formElement.invitationMessage).getValue();
    var progressCallback = function(err) {
      if (err) {
        addFailCount++;
      } else {
        addSuccessCount++;
      }
      
      var countText = addSuccessCount;
      if (addFailCount > 0) {
        countText += ' ' + getLocale().getText("panelAdmin.block.invitations.sendingInvitationFailed", addFailCount);
      }
      
      var text = getLocale().getText("panelAdmin.block.invitations.sendingInvitations", countText, invitationCount);
      loadItem.updateText(text);
    };
    
    for (var i = 0; i < invitationCount; i++) {
      var userId = $(this._formElement['inviteUser.' + i + '.id']).getValue();
      var email = $(this._formElement['inviteUser.' + i + '.email']).getValue();
      addRequests.push(this._createInvitationRequest(invitationMessage, userId, queryId, email, progressCallback));
    }
    
    async.parallelLimit(addRequests, 5, function (err, results) {
      if (err) {
        this._showError(err);
      } else {
        var errorMails = [];
        var successCount = 0;
        
        for (var i = 0; i < results.length; i++) {
          var result = results[i];
          if (result && result.error) {
            errorMails.push(result.email);
          } else {
            successCount++;
          }
        }
        
        if (successCount) {
          this._showSuccess(getLocale().getText('panelAdmin.block.invitations.invitationSuccess', successCount));
        }
        
        if (errorMails.length) {
          this._showError(getLocale().getText('panelAdmin.block.invitations.invitationFailures', errorMails.join(',')));
        }
        
        document.fire("ed:invitationListRefreshRequired");
        this._invitationList.clear();
      }
      
      endLoadingOperation();
    }.bind(this));
  },
  
  _onSaveButtonClick: function (event) {
    Event.stop(event);
    
    if ($(this._formElement.addUsers).getValue() == '1') {
      this._addUsers();
    } else {
      var invitationMessage = $(this._formElement.invitationMessage).getValue();
      
      JSONUtils.request(CONTEXTPATH + '/panel/admin/validateinvitation.json', {
        parameters: {
          invitationMessage: invitationMessage
        },
        onSuccess : function() {
          this._sendInvitations();
        }.bind(this)
      });  
      
     
    }
  },
  _onResendInvitations: function (event) {
    var rowElements = event.memo.rowElements;
    for (var i = 0; i < rowElements.length; i++) {
      var userId = rowElements[i].down('input[name="userId"]').value;
      var firstName = rowElements[i].down('input[name="firstName"]').value;
      var lastName = rowElements[i].down('input[name="lastName"]').value;
      var email = rowElements[i].down('input[name="email"]').value;
      if (!this._invitationList.contains(userId, email)) {
        this._invitationList.add(userId, firstName, lastName, email);
      }
    } 
  },
  _onCSVButtonClick: function (event) {
    startLoadingOperation("panelAdmin.block.invitations.loadingCsv");
    this._formElement.action = CONTEXTPATH + '/panel/admin/loadinvitationfile.json';
    this._formElement.submit();
  },
  _onCSVLoaded: function (event) {
    endLoadingOperation();
    var jsonDocument = $('csvFileContent').contentDocument || $('csvFileContent').contentWindow.document;
    if (jsonDocument.body && jsonDocument.body.firstChild) {
      var jsonResponse = eval('(' + jsonDocument.body.firstChild.innerHTML + ')');
      JSONUtils.showMessages(jsonResponse);
      for (var i = 0, l = jsonResponse.users.length; i < l; i++) {
        if (!this._invitationList.contains(jsonResponse.users[i].id, jsonResponse.users[i].email)) {
          this._invitationList.add(jsonResponse.users[i].id, jsonResponse.users[i].firstName, jsonResponse.users[i].lastName, jsonResponse.users[i].email);
        }
      }
    }
  }
});

// AutoCompleteList

AutoCompleteList = Class.create({
  initialize: function(blockController) {
    this._blockController = blockController;
    this._entries = new Array();
    this._guiElement = new Element("div", {
      className: "invitationAutoCompleteList"
    });
    this._guiElement.setStyle({
      display: 'none'
    });
  },
  add: function(id, firstName, lastName, email) {
    var entry = new AutoCompleteEntry(this._blockController);
    entry.setId(id);
    entry.setFirstName(firstName);
    entry.setLastName(lastName);
    entry.setEmail(email);
    entry.refreshDisplayValue();
    this._entries.push(entry);
    this._guiElement.appendChild(entry.getGuiElement());
  },
  clear: function () {
    for (var i = this._entries.length - 1; i >= 0; i--) {
      this._entries[i].deinitialize();
      this._guiElement.removeChild(this._entries[i].getGuiElement());
    }
    this._entries = [];
  },
  get: function(index) {
    return this._entries[index];
  },
  getSize: function() {
    return this._entries.length;
  },
  isVisible: function() {
    return this._guiElement.visible();
  },
  setVisible: function(visible) {
    this._guiElement.setStyle({
      display: visible ? '' : 'none'
    });
  },
  getGuiElement: function() {
    return this._guiElement;
  }
});

// AutoCompleteEntry

AutoCompleteEntry = Class.create({
  initialize: function(blockController) {
    this._blockController = blockController;
    this._id = null;
    this._firstName = null;
    this._lastName = null;
    this._email = null;
    this._guiElement = new Element("div", {
      className: "invitationAutoCompleteEntry"
    });
    this._displayNameElement = new Element("div", {
      className: "autoCompleteEntryNameContainer"
    });
    this._displayEmailElement = new Element("div", {
      className: "autoCompleteEntryEmailContainer"
    });
    this._guiElement.appendChild(this._displayNameElement);
    this._guiElement.appendChild(this._displayEmailElement);
    this._entryClickListener = this._onEntryClick.bindAsEventListener(this);
    Event.observe(this._guiElement, "click", this._entryClickListener);
  },
  deinitialize: function() {
    Event.stopObserving(this._guiElement, "click", this._entryClickListener);
  },
  refreshDisplayValue: function() {
    var name = '';
    if (this._firstName) {
      name += this._firstName;
    }
    if (this._lastName) {
      if (name != '') {
        name += ' ';
      }
      name += this._lastName;
    }
    this._displayNameElement.update(name);
    if (this._email) {
      this._displayEmailElement.update(this._email);
    }
  },
  setId: function(id) {
    this._id = id;
  },
  setFirstName: function(firstName) {
    this._firstName = firstName;
  },
  setLastName: function(lastName) {
    this._lastName = lastName;
  },
  setEmail: function(email) {
    this._email = email;
  },
  getId: function() {
    return this._id;
  },
  getFirstName: function() {
    return this._firstName;
  },
  getLastName: function() {
    return this._lastName;
  },
  getEmail: function() {
    return this._email;
  },
  getGuiElement: function() {
    return this._guiElement;
  },
  _onEntryClick: function (event) {
    this._blockController.autoCompleteEntryClicked(this);
  }
});

// InvitationList

InvitationList = Class.create({
  initialize: function (blockController, guiElement) {
    this._blockController = blockController;
    this._guiElement = guiElement;
    this._entries = new Array();
  },
  getSize: function() {
    return this._entries.length;
  },
  add: function(id, firstName, lastName, email) {
    var entry = new InvitationEntry(this._blockController);
    entry.setId(id);
    entry.setFirstName(firstName);
    entry.setLastName(lastName);
    entry.setEmail(email);
    entry.refreshDisplayValue();
    this._entries.push(entry);
    this._guiElement.appendChild(entry.getGuiElement());
    entry.setIndex(this._entries.length - 1);
  },
  remove: function (entry) {
    var index = this.indexOf(entry);
    if (index >= 0) {
      this._entries.splice(index, 1);
      this._guiElement.removeChild(entry.getGuiElement());
      entry.deinitialize();
      for (var i = index, l = this._entries.length; i < l; i++) {
        this._entries[i].setIndex(i);
      }
    }
  },
  clear: function () {
    for (var i = this._entries.length - 1; i >= 0; i--) {
      this._entries[i].deinitialize();
      this._guiElement.removeChild(this._entries[i].getGuiElement());
    }
    this._entries = [];
  },
  contains: function(id, email) {
    for (var i = 0, l = this._entries.length; i < l; i++) {
      var cmpEntry = this._entries[i];
      if (id && id === cmpEntry.getId()) {
        return true;
      }
      else if (!id && !cmpEntry.getId() && email === cmpEntry.getEmail()) {
        return true;
      }
    }
    return false;
  },
  indexOf: function (entry) {
    for (var i = 0, l = this._entries.length; i < l; i++) {
      if (this._entries[i] === entry) {
        return i;
      }
    }
    return -1;
  }
});

// InvitationEntry

InvitationEntry = Class.create({
  initialize: function (blockController) {
    this._blockController = blockController;
    this._id = null;
    this._name = null;
    this._guiElement = new Element("div", {
      className: "invitationEntry"
    });
    var idElement = new Element("input", {
      type: "hidden",
      className: "userIdContainer"
    });
    var firstNameElement = new Element("input", {
      type: "hidden",
      className: "userFirstNameContainer"
    });
    var lastNameElement = new Element("input", {
      type: "hidden",
      className: "userLastNameContainer"
    });
    var emailElement = new Element("input", {
      type: "hidden",
      className: "userEmailContainer"
    });
    this._displayNameElement = new Element("div", {
      className: "invitationEntryNameContainer"
    });
    this._displayEmailElement = new Element("div", {
      className: "invitationEntryEmailContainer"
    });
    this._removeElement = new Element("div", {
      className: "invitationEntryRemoveContainer"
    });
    //this._removeElement.update('X');
    this._guiElement.appendChild(idElement);
    this._guiElement.appendChild(firstNameElement);
    this._guiElement.appendChild(lastNameElement);
    this._guiElement.appendChild(emailElement);
    this._guiElement.appendChild(this._displayNameElement);
    this._guiElement.appendChild(this._displayEmailElement);
    this._guiElement.appendChild(this._removeElement);
    this._removeEntryClickListener = this._onRemoveEntryClick.bindAsEventListener(this);
    Event.observe(this._removeElement, "click", this._removeEntryClickListener);
  },
  deinitialize: function() {
    Event.stopObserving(this._removeElement, "click", this._removeEntryClickListener);
  },
  getGuiElement: function() {
    return this._guiElement;
  },
  setIndex: function (index) {
    this._guiElement.down("input.userIdContainer").name = 'inviteUser.' + index + '.id';
    this._guiElement.down("input.userFirstNameContainer").name = 'inviteUser.' + index + '.firstName';
    this._guiElement.down("input.userLastNameContainer").name = 'inviteUser.' + index + '.lastName';
    this._guiElement.down("input.userEmailContainer").name = 'inviteUser.' + index + '.email';
  },
  refreshDisplayValue: function () {
    var name = '';
    if (this._firstName) {
      name += this._firstName;
    }
    if (this._lastName) {
      if (name != '') {
        name += ' ';
      }
      name += this._lastName;
    }
    this._displayNameElement.update(name);
    if (this._email) {
      this._displayEmailElement.update(this._email);
    }
  },
  setId: function (id) {
    this._id = id;
    this._guiElement.down("input.userIdContainer").value = id;
  },
  setFirstName: function (firstName) {
    this._firstName = firstName;
    this._guiElement.down("input.userFirstNameContainer").value = firstName;
  },
  setLastName: function (lastName) {
    this._lastName = lastName;
    this._guiElement.down("input.userLastNameContainer").value = lastName;
  },
  setEmail: function (email) {
    this._email = email;
    this._guiElement.down("input.userEmailContainer").value = email;
  },
  getId: function() {
    return this._id;
  },
  getFirstName: function() {
    return this._firstName;
  },
  getLastName: function() {
    return this._lastName;
  },
  getEmail: function() {
    return this._email;
  },
  _onRemoveEntryClick: function (event) {
    this._blockController.invitationEntryClicked(this);
  }
});

addBlockController(new InviteActionsBlockController());