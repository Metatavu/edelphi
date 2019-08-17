import * as React from "react";
import { BrowserRouter } from "react-router-dom";
import MainPage from "./main";

/**
 * App component
 */
class App extends React.Component {

  /**
   * Render method for app component
   */
  public render() {
    return (
      <div className="App">
        <BrowserRouter>
          <MainPage />
        </BrowserRouter>
      </div>
    );
  }
}

export default App;