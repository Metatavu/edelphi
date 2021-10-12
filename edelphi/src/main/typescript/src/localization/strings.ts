import LocalizedStrings, { 
  LocalizedStringsMethods
} from 'localized-strings';

export interface IStrings extends LocalizedStringsMethods {

  generic: {
    loading: string;
    back: string;
    ok: string;
    eDelphi: string;
    welcomeUser: string;
    profileLink: string;
    logoutLink: string;
    panelAdminBreadcrumb: string;
    select: string;
  },

  errorDialog: {
    header: string;
    reloadPage: string;
    unsavedContents: string;
    reportIssue: string;
    technicalDetails: string;
    time: string;
    url: string;
    errorMessage: string;
    close: string
    reload: string;
  },

  confirmationDialog: {
    title: string;
    content: string;
    confirm: string;
    cancel: string;
  }

  panel: {
    query: {
      previous: string;
      next: string;
      save: string;
      skip: string;
      noAnswer: string;
      quickNavigationTitle: string;
      live2d: {
        statistics: {
          title: string;
          axisX: string;
          axisY: string;
          answerCount: string;
          lowerQuartile: string;
          median: string;
          upperQuartile: string 
        }
      },
      commentEditor: {
        title: string;
        save: string;
        modify: string
      },
      comments: {
        title: string;
        reply: string;
        elaborate: string;
        hide: string;
        show: string;
        edit: string;
        remove: string;
        commentModified: string;
        commentDate: string;
        saveReply: string;
        saveEdit: string;
        confirmRemoveConfirm: string;
        confirmRemoveCancel: string;
        confirmRemoveText: string;
        noComments: string;
        yourComment: string;
        selectAmount: string;
        showReplies: string;
        hideReplies: string;
        newCommentCount: string;
        oldestFirst: string;
        newestFirst: string;
      }
    }
  },

  panelAdmin: {
    navigation: {
      panel: string;
      administration: string;
      reportAnIssue: string
    },
    queryEditor: {
      removeQueryAnswersConfirm: string;

      queryCommentOptions: {
        title: string;
        categories: string;
        addCategory: string;
        save: string;
        close: string;
        deleteCategory: string;
        deleteCategoryConfirm: string
      },
      
      pageCommentOptions: {
        title: string;
        categories: string;
        addCategory: string;
        save: string;
        close: string;
        deleteCategory: string;
        deleteCategoryConfirm: string
      },

      pageLive2dOptions: {
        title: string;
        save: string;
        close: string;
        visible: string;
        visibleOptions: {
          AFTEROWNANSWER: string
          IMMEDIATELY: string
        }
      },

      anonymousLoginDialog: {
        title: string;
        helpText: string
        hintText: string;
        linkLabel: string;
        downloadImage: string;
        downloadPrintableImage: string;
        okButton: string
      },

      copyQueryDialog: {
        title: string;
        helpText: string;
        nameLabel: string;
        targetPanelLabel: string;
        withDataOption: string;
        withoutDataOption: string;
        okButton: string;
        cancelButton: string;
        newName: string;
        emailDialogTitle: string;
        emailDialogMessage: string
        validationErrorDialogTitle: string;
      }

    },
    liveView: {
      querySelectLabel: string;
      pageSelectLabel: string;
      commentCategorySelectLabel: string;
      defaultCategory: string;
      replyCount: string;
      answersTab: string;
      commentsTab: string;
      selectQueryAndPage: string
    },
    reports: {
      title: string;
      queriesListTitle: string;
      queriesListDates: string;
      exportFilter: string;
      exportFilterByExpertise: string;
      exportFilterByUserGroup: string;
      exportFilterByPage: string;
      exportFilterByPageAll: string;
      exportFilterByCommentCategory: string;
      exportReport: string;
      exportReportContents: string;
      exportReportPDF: string;
      exportReportGoogleDocument: string;
      exportCharts: string;
      exportChartsPNG: string;
      exportData: string;
      exportDataCSV: string;
      exportDataGoogleSpreadsheet: string;
      reportToEmailTitle: string;
      reportToEmailMessageDelivery: string
      reportToEmailMessageDeliveryTime: string;
      reportToEmailMessageLeave: string
    },

    inviteUsers: {
      title: string;
      breadcrumb: string;
      passwordHeader: string;
      passwordText: string;
      invitationsSentHeader: string;
      invitationsSentText: string;

      inviteBlock: {
        title: string;
        emailPlaceholder: string;
        csvFieldLabel: string;
        csvUploadButton: string;
        csvExampleLinkLabel: string;
        usersToBeInvitedLabel: string;
        invitationFieldLabel: string;
        addUsersWithoutInvitationLabel: string;
        singleUseLabel: string;
        sendInvitationsButtonLabel: string;
        invitationTarget: string;
        panelTarget: string;
        addUser: string;
        mailTemplate: string;
        acceptReplaceMissing: string;
        acceptReplace: string;
      },
      usersListBlock: {
        title: string;
        addedByManagerLabel: string;
        addedDateTimeLabel: string;
        resendInvitationToAllInPage: string;
        resendInvitationToUser: string;

        lists: {
          ADDED: {
            title: string;
            timeLabel: string;
          };
          
          IN_QUEUE: {
            title: string;
            timeLabel: string;
          };
          BEING_SENT: {
            title: string;
            timeLabel: string;
          };
          SEND_FAIL: {
            title: string;
            timeLabel: string;
          };
          PENDING: {
            title: string;
            timeLabel: string;
          };
          ACCEPTED: {
            title: string;
            timeLabel: string;
          };
          DECLINED: {
            title: string;
            timeLabel: string;
          };
        }
      }
    }
  }
  
}

const strings: IStrings = new LocalizedStrings({
  en: require("./en.json"),
  fi: require("./fi.json")
});

export default strings;