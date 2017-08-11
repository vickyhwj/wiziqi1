var cb, len = 7;
class UserRow extends React.Component {
    constructor() {

    }
    sendAdd() {
        var obj = new Object();

        obj.type = 1;
        obj.to = this.props.username;
        obj = JSON.stringify(obj);
        websocket.send(obj);

    }
    render() {
        return (
            <tr>
                <th scope="row">{this.props.num}</th>
                <td>{this.props.username}</td>
                <td>{this.props.win}</td>
                <td>{this.props.fail}</td>
                <td><button onClick={() => { this.sendAdd() }}>go</button></td>
            </tr>
        )
    }
}
class UserList extends React.Component {
    constructor() {
        this.state = {
            userList: [

            ],

        }
    }

    componentDidMount() {

    }
    render() {
        var index = 0;
        var list = this.state.userList.map(function (params) {
            return (
                <UserRow username={params.userid} win={params.win} fail={params.fail} num={++index} />
            )
        })
        return (
            <div className="bs-example" data-example-id="hoverable-table">
                <table className="table table-hover">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Username</th>
                            <th>Win</th>
                            <th>Lose</th>
                            <th>添加好友</th>
                        </tr>
                    </thead>
                    <tbody>
                        {list}
                    </tbody>
                </table>
            </div>
        )

    }
}
class PagerUl extends React.Component {
    constructor() {
        this.state = {
            now: 1,
            sum: 0,
            last: 1,
            next: 1,
            userid: ''
        }
    }
    dumpPage(page) {
        var v = $('#searchInput').val();

        $.ajax({
            type: "get",
            url: "getUserListByUserId",
            data: "userId=" + v + "&index=" + page + "&len=" + len,

            success: (response) => {
                var re = JSON.parse(response);

                x.setState({ userList: re.userlist });
                p.setState({ now: re.now, sum: re.sum, last: re.now - 1, next: re.now + 1, userid: $('#searchInput').val() })
            }
        });
    }
    ipDump() {
        var ip = this.refs.ip.getDOMNode();
        this.dumpPage(ip.value);
    }
    render() {
        return (
            <div>
                <ul className="pager">
                    <li><a href="#" onClick={() => { this.dumpPage(this.state.last) }}>Previous</a></li>
                    <li>{this.state.now}</li>
                    <li><a href="#" onClick={() => { this.dumpPage(this.state.next) }}>Next</a></li>
                    <li>共{this.state.sum}页</li>
                    <li><input type="text" placeholder="dump" ref="ip" /></li>
                    <li><button onClick={() => { this.ipDump() }}>go</button></li>
                </ul>
            </div>
        )
    }
}
var x = React.render(
    <UserList />, document.getElementById("userList")
)
var p = React.render(
    <PagerUl />,
    document.getElementById("pageDiv")
)
$("#searchButton").click(function (e) {
    e.preventDefault();
    var v = $('#searchInput').val();

    $.ajax({
        type: "get",
        url: "getUserListByUserId",
        data: "userId=" + v + "&index=1&len=" + len,

        success: (response) => {
            var re = JSON.parse(response);

            x.setState({ userList: re.userlist });
            p.setState({ now: re.now, sum: re.sum, last: re.now - 1, next: re.now + 1, userid: $('#searchInput').val() })
        }
    });

});

class Message extends React.Component {
    isRead() {
        var obj = new Object();
        obj.msgId = this.props.msgId;
        obj.type = 0;
        obj = JSON.stringify(obj);
        websocket.send(obj);
        this.refs.dd.getDOMNode().remove();
    }
    sendOk() {
        var obj = new Object();
        obj.to = this.props.from;
        obj.type = 2;
        obj = JSON.stringify(obj);
        websocket.send(obj);
        this.refs.bu.getDOMNode().innerHTML = '已添加';
    }
    sendNo() {
        var obj = new Object();
        obj.to = this.props.from;
        obj.type = 3;
        obj = JSON.stringify(obj);
        websocket.send(obj);
        this.refs.bu.getDOMNode().innerHTML = '已拒绝';
    }
    render() {
        var createDate = this.props.createDate;
        var title, content;
        switch (this.props.type) {
            case 1:
                title = "添加好友请求";
                content = this.props.from + "想添加你为好友!" + this.props.content;
                break;
            case 2:
                title = "添加好友请求";
                content = this.props.from + "同意了!" + this.props.content;
                break;
            case 3:
                title = "添加好友请求";
                content = this.props.from + "拒绝了!" + this.props.content;
                break;

        }
        if (this.props.type == 1)
            return (
                <div ref="dd">
                    <h6>{title}(({new Date(createDate.time).toString()})<button className="removebu" onClick={() => { this.isRead() }}><span className="glyphicon glyphicon-remove"></span></button></h6>
                    <div className="fengexian"></div>
                    <p>{content}</p>
                    <div ref="bu">
                        <button onClick={() => { this.sendOk() }}>yes</button>
                        <button onClick={() => { this.sendNo() }}>no</button>
                    </div>
                </div>
            )
        else if (this.props.type == 2 || this.props.type == 3)
            return (
                <div ref="dd">
                    <h6>{title}({new Date(createDate.time).toString()})<button className="removebu" onClick={() => { this.isRead() }}><span className="glyphicon glyphicon-remove"></span></button></h6>
                    <div className="fengexian"></div>
                    <p>{content}</p>

                </div>
            )
    }

}
var m1;
class MessageList extends React.Component {
    constructor() {
        m1 = JSON.parse(message);
        this.state = { messageList: m1.messages }
    }
    render() {
        var list = this.state.messageList.map(function (params) {

            return (
                <Message type={params.type} from={params.from} content={params.content} msgId={params.msgId}
                    createDate={params.createDate} />

            )
        })
        return (
            <div >
                {list}
            </div>
        )
    }
}
var ml = React.render(
    <MessageList />,
    document.getElementById("con12")
)

websocket.onmessage = function (event) {
    var data = JSON.parse(event.data);
    var type = data.type;
    if (type == 1 || type == 2 || type == 3) {
        var messageList = ml.state.messageList;
        messageList.push(data
        )
        ml.setState({
            messageList: messageList
        })
    }

}

class FriendList extends React.Component {

    constructor() {
        this.state = { userids: [] };
        $.ajax({
            type: "get",
            url: "getFridendListJSON",
            data: "userA="+userid,
          
            success:  (response) =>{
                var data=JSON.parse(response);

                this.setState({userids:data.userids})
            }
        });
    }
    render() {
        var list = this.state.userids.map(function (params) {
            return (
                <UserItem userid={params.userid} />
            );
        })
        return (
            <ul className="list-group">
                {list}
            </ul>
        );
    }
}
class UserItem extends React.Component {
    del(){
        $.ajax({
            type: "get",
            url: "deleteRelationship",
            data: "userA="+userid+"&userB="+this.props.userid,
            success:  (response)=>{
                this.refs.item.getDOMNode().remove();
            }
        });
    }

    constructor() {

    }
    render() {

        return (
            <li className="list-group-item" ref="item">{this.props.userid}<button onClick={()=>{this.del()}}>del</button></li>
        );
    }
}
var friendList = React.render(
    <FriendList />,
    document.getElementById("friendList")
)
$("#refresh").click(function(e){
       $.ajax({
            type: "get",
            url: "getFridendListJSON",
            data: "userA="+userid,
          
            success:  (response) =>{
                var data=JSON.parse(response);

                friendList.setState({userids:data.userids})
            }
        });
})