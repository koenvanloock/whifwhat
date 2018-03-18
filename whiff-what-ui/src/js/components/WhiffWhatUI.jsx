import React from "react";
import AwesomeComponent from "./AwesomeComponent";
import Navbar from "./Navbar"
class WhiffWhatUI extends React.Component {
  constructor(props) {
    super(props);
  }


  render() {
    return (
        <div>
          <Navbar/>
          <AwesomeComponent/>
        </div>
    );
  }


}
export default WhiffWhatUI;
