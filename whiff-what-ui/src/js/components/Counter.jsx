import React from "react"
import RaisedButton from "material-ui/RaisedButton"

class Counter extends React.Component {

  constructor(props) {
    super(props);
    this.state = {likesCount: 0};
    this.onLike = this.onLike.bind(this)
  }

  onLike() {
    let newLikesCount = this.state.likesCount + 1;
    this.setState({likesCount: newLikesCount});
  }

  render() {
    return (
        <div>
          Likes:<span>{this.state.likesCount}</span>
          < div >< RaisedButton
              label="Like"
              onClick={this.onLike}/></div>
        </div>
    )

  }
}

export default Counter;


