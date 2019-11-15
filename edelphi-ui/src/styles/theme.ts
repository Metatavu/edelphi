import { createMuiTheme, responsiveFontSizes } from "@material-ui/core";
import { light } from "@material-ui/core/styles/createPalette";

let theme = createMuiTheme();
theme = responsiveFontSizes(theme);

export default createMuiTheme({
  palette: {
    primary: {
      main: "#186089",
      light: "",
      dark: "#26201E",
      contrastText: "#fff"
    },
    secondary: { 
      main: "#039a03",
      light: "",
      dark: "",
      contrastText: "#4c4c4c"
    },
    background: {
      default: "#fff",
      paper: "#F5EFEA"
    },
    text: {
      primary: "#1e1e1e",
      secondary: "#df4d1c",
      disabled: "#ddd",
      hint: "#0099ff"
    }
  },
  typography: {
    fontFamily: "'PT Sans', Arial, sans-serif",
    h1: {
      [theme.breakpoints.up("md")]: {}
    },
    h2: {
      fontSize: "1.45em"
    },
    h3: {},
    h4: {},
    body1: {},
    body2: {
      fontSize: "0.875em",
      lineHeight: "1.45em"
    },
    subtitle1: {},
    subtitle2: {},
    caption: {
      fontStyle: "italic",
      fontSize: "1em"
    },
    button: {}
  },
  overrides: {
    MuiButton: {
      containedPrimary: {
        borderRadius: 3,
        textTransform: "initial",
        padding: "5px 15px",
        boxShadow: "0 0 8px rgba(0, 0, 0, 0.2)",
          "&:hover": {
            boxShadow: "0 0 10px rgba(0, 0, 0, 0.2)"
          },
          "&:active": {
            boxShadow: "0 0 0px rgba(0, 0, 0, 0.2)"
          }
      }
    },
  }
});