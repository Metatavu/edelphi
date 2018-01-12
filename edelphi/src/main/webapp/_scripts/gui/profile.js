/* global BlockController, startLoadingOperation */

var ProfileSettingsBasicInfoEditor = Class.create(BlockController, {
  initialize : function(editorContainer) {
    this._editorContainer = editorContainer;
    this._updateProfileButtonClickListener = this._onUpdateProfileButtonClick.bindAsEventListener(this);
    this._changeProfilePictureButtonClickListener = this._onUpdateProfilePictureButtonClick.bindAsEventListener(this);
    this._closeModalButtonClickListener = this._onCloseModalButtonClick.bindAsEventListener(this);
    this._imageLoadedListener = this._onImageLoaded.bindAsEventListener(this);
    this._deleteInvitationClickListener = this._onDeleteInvitationClick.bindAsEventListener(this);
    this.setup();
  },
  deinitialize: function () {
    Event.stopObserving(this._saveButton, 'click', this._updateProfileButtonClickListener);
    Event.stopObserving($('_uploadFrame'), 'load', this._imageLoadedListener);
    var _this = this;
    $('profileInvitationBlockContent').select('.profileInvitationRowWrapper').each(function(invitationElement) {
      var deleteElement = invitationElement.down('.blockContextualLink.delete');
      Event.stopObserving(deleteElement, 'click', _this._deleteInvitationClickListener);
    });
  },
  setup: function () {
    this._saveButton = this._editorContainer.down('input[name="updateProfileButton"]');
    Event.observe(this._saveButton, 'click', this._updateProfileButtonClickListener);
    
    this._profilePicture = $('profilePicture');
    this._changeProfilePictureButton = this._profilePicture.down('.changeProfilePictureButton');
    Event.observe(this._changeProfilePictureButton, 'click', this._changeProfilePictureButtonClickListener);

    this._closeModalButton = this._profilePicture.down('.changeProfilePictureCloseModalButton');
    Event.observe(this._closeModalButton, 'click', this._closeModalButtonClickListener);
    
    Event.observe($('_uploadFrame'), 'load', this._imageLoadedListener);
    
    // Invitations
    
    var invitationBlock = $('profileInvitationBlockContent');
    if (invitationBlock) {
      var _this = this;
      invitationBlock.select('.profileInvitationRowWrapper').each(function(invitationElement) {
        var deleteElement = invitationElement.down('.blockContextualLink.delete');
        Event.observe(deleteElement, 'click', _this._deleteInvitationClickListener);
      });
    }
  },
  hideEditor: function () {
    var dialogElement = this._profilePicture.down('.changeProfilePictureModalOverlay');
    var contentContainer = this._profilePicture.down('.changeProfilePictureModalContainer');

    dialogElement.hide();
    contentContainer.hide();
    
    var fileInput = this._profilePicture.down('input[name="imageData"]');
    if (fileInput)
      fileInput.value = '';
  },
  _onDeleteInvitationClick: function (event) {
    Event.stop(event);

    var linkElement = Event.element(event);
    var linkHref = linkElement.getAttribute('href');
    var hashParams = this._parseHash(linkHref);
    var invitationId = hashParams.get('invitationId');
    
    var titleContainer = new Element('div', { className: 'modalPopupTitleContent' });
    titleContainer.update(getLocale().getText('profile.block.deleteInvitationMessage'));

    var popup = new ModalPopup({
      content: titleContainer,
      buttons: [
        {
          text: getLocale().getText('profile.block.deleteInvitationCancel'),
          action: function(instance) {
            instance.close();
          }
        },
        {
          text: getLocale().getText('profile.block.deleteInvitationDelete'),
          classNames: 'modalPopupButtonRed',
          action: function(instance) {
            instance.close(true);
            JSONUtils.request(CONTEXTPATH + '/archiveinvitation.json', {
              parameters: {
                invitationId: invitationId
              },
              onSuccess : function() {
                var invitationElement = linkElement.up('.profileInvitationRowWrapper');
                if (invitationElement != null)
                  invitationElement.remove();
              }
            });
          }
        }
      ]
    });

    popup.open(linkElement);
  },
  _onUpdateProfileButtonClick: function (event) {
    Event.stop(event);
    
    var parameters = {
      userId: this._editorContainer.down('input[name="userId"]').value,
      firstName: this._editorContainer.down('input[name="firstName"]').value,
      lastName: this._editorContainer.down('input[name="lastName"]').value,
      nickname: this._editorContainer.down('input[name="nickname"]').value,
      emailId: this._editorContainer.down('input[name="emailId"]').value,
      email: this._editorContainer.down('input[name="email"]').value,
      commentMail: this._editorContainer.down('input[name="commentMail"]').checked ? '1' : '0'
   };
    
    startLoadingOperation('profile.block.savingProfile');
    JSONUtils.request(CONTEXTPATH + '/profile/saveprofile.json', {
      parameters: parameters,
      onComplete: function () {
        endLoadingOperation();
      }
    });
  },
  _onUpdateProfilePictureButtonClick: function (event) {
    Event.stop(event);

    var dialogElement = this._profilePicture.down('.changeProfilePictureModalOverlay');
    var contentContainer = this._profilePicture.down('.changeProfilePictureModalContainer');
    dialogElement.show();
    contentContainer.show();
  },
  _onCloseModalButtonClick: function () {
    this.hideEditor();
  },
  _onImageLoaded: function () {
    var jsonDocument = $('_uploadFrame').contentDocument || $('_uploadFrame').contentWindow.document;
    if (jsonDocument.body && jsonDocument.body.firstChild) {
      var responseText = jsonDocument.body.firstChild.innerHTML;
      if (!responseText) {
        responseText = jsonDocument.body.firstChild.textContent;
      }
      var jsonResponse = JSON.parse(responseText);
      JSONUtils.showMessages(jsonResponse);
      var imageContainer = $('profilePicture');
      if (imageContainer) {
        var imageUrl = CONTEXTPATH + '/user/picture.binary?userId=' + $('profileUserIdElement').value + '&time=' + new Date().getTime();
        imageContainer.setStyle({
          backgroundImage : 'url("' + imageUrl + '")'
        }); 
      }
      this.hideEditor();
    }
  }
});

document.observe('dom:loaded', function() {
  new ProfileSettingsBasicInfoEditor($('profileSettingsForm'));
});
