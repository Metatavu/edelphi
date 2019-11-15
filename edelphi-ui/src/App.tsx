import * as React from "react";
import { BrowserRouter, Route } from "react-router-dom";
import Query from "./screens/panel/query";
import { CssBaseline } from "@material-ui/core";
import { ThemeProvider } from "@material-ui/styles";
import theme from "../styles/theme";
/**
 * Interface representing component properties
 */
interface Props {
}
/**
 * Interface representing component state
 */
interface State {
}
/**
 * App component
 */
export default class App extends React.Component<Props, State> {
  /**
   * Component render method
   */
  public render() {
    return (
      <ThemeProvider theme={theme}>
        <CssBaseline />
        <BrowserRouter>
          <div className="App">
            <Route
              path="/:panelSlug/:querySlug"
              exact={ true }
              render={ (props) => (
                <Query  panelSlug={ props.match.params.panelSlug as string } querySlug={ props.match.params.querySlug as string }/>
              )}
            />
          </div>
        </BrowserRouter>
      </ThemeProvider>
    );
  }
}
