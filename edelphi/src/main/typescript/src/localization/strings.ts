import LocalizedStrings, { 
  LocalizedStringsMethods
} from 'localized-strings';

export interface IStrings extends LocalizedStringsMethods {

  generic: {
    loading: string,
    back: string
  },

  errorDialog: {
    header: string,
    errorOccurred: string,
    close: string
  },

  panel: {
    query: {
      previous: string,
      next: string,
      save: string,
      skip: string,
      noAnswer: string,
      quickNavigationTitle: string,
      live2d: {
        statistics: {
          title: string,
          axisX: string,
          axisY: string,
          answerCount: string,
          lowerQuartile: string,
          median: string,
          upperQuartile: string 
        }
      },
      commentEditor: {
        title: string,
        save: string,
        modify: string
      },
      comments: {
        title: string,
        reply: string,
        ellaborate: string,
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
        confirmRemoveText: string,
        noComments: string,
        yourComment: string
      }
    }
  },

  panelAdmin: {
    queryEditor: {
      pageCommentOptions: {
        title: string,
        categories: string,
        addCategory: string,
        save: string,
        close: string,
        deleteCategory: string,
        deleteCategoryConfirm: string
      },
      pageLive2dOptions: {
        title: string,
        save: string,
        close: string,
        visible: string,
        visibleOptions: {
          AFTEROWNANSWER: string
          IMMEDIATELY: string
        }
      }
    },
    commentView: {
      querySelectLabel: string,
      pageSelectLabel: string,
      categorySelectLabel: string,
      defaultCategory: string
    }
  }
  
}

const strings: IStrings = new LocalizedStrings({
  en: require("./en.json"),
  fi: require("./fi.json")
});

export default strings;