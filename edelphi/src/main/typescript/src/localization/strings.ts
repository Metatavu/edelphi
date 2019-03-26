import LocalizedStrings, { 
  LocalizedStringsMethods
} from 'localized-strings';

export interface IStrings extends LocalizedStringsMethods {

  panel: {
    query: {
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
  }
  
}

const strings: IStrings = new LocalizedStrings({
  en: require("./en.json"),
  fi: require("./fi.json")
});

export default strings;