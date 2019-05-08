import LocalizedStrings, { 
  LocalizedStringsMethods
} from 'localized-strings';

export interface IStrings extends LocalizedStringsMethods {

  panel: {
    query: {
      commentEditor: {
        title: string,
        save: string
      },
      comments: {
        title: string,
        reply: string,
        hide: string,
        show: string,
        edit: string,
        remove: string,
        commentModified: string,
        commentDate: string,
        saveReply: string,
        saveEdit: string,
        confirmRemoveConfirm: string,
        confirmRemoveCancel: string,
        confirmRemoveText: string
      }
    }
  },

  panelAdmin: {
    queryEditor: {
      pageCommentOptions: {
        title: string,
        addCategory: string,
        save: string
      }
    }
  }
  
}

const strings: IStrings = new LocalizedStrings({
  en: require("./en.json"),
  fi: require("./fi.json")
});

export default strings;