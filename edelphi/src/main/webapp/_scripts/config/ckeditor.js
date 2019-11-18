(function() {
  'use strict';

  CKEDITOR.plugins.addExternal('fnigenericbrowser', '/_scripts/ckplugins/fnigenericbrowser/');
  CKEDITOR.plugins.addExternal('fnidynlist', '/_scripts/ckplugins/fnidynlist/');
  
  CKEDITOR.config.scayt_autoStartup = false;
  CKEDITOR.config.entities = false;
  CKEDITOR.config.autoGrow_onStartup = true;
  CKEDITOR.config.autoGrow_minHeight = 300;
  CKEDITOR.config.autoGrow_maxHeight = 600;
  CKEDITOR.config.forcePasteAsPlainText = true;
  CKEDITOR.config.language = getLocale().getLanguage();
  
  CKEDITOR.config.extraPlugins = 'autogrow,mediaembed,fnigenericbrowser';
  
  CKEDITOR.config.removePlugins = 'elementspath';
  CKEDITOR.config.toolbarCanCollapse = false;
  CKEDITOR.config.resize_dir = 'vertical';
  
  CKEDITOR.config.toolbar_materialToolbar = [
    ['Cut','Copy','Paste','PasteText','PasteFromWord','-', 'Scayt'],
    ['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
    ['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
    ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
    ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
    ['Link','Unlink'],
    ['Image','MediaEmbed','Flash','Table','HorizontalRule','SpecialChar'],
    ['FontSize', 'Format'],
    ['Maximize', 'ShowBlocks','-','About', 'Source']
  ];
  
  CKEDITOR.config.toolbar_thesisDescriptionToolbar = [
    ['Bold','Italic','Underline'],['Link','Unlink'],['Image','MediaEmbed'],['Table'],['NumberedList','BulletedList'], ['Paste','PasteText','PasteFromWord'], ['Source']
  ];
  
  CKEDITOR.config.toolbar_expertiseDescriptionToolbar = [
    ['Bold','Italic','Underline'],['Link','Unlink'],['Image','MediaEmbed'],['Table'],['NumberedList','BulletedList']
  ];
  
  CKEDITOR.config.toolbar_simpleToolbar = [
    ['Bold','Italic','Underline']
  ];

}).call(this);