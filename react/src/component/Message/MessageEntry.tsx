import MessageChat from "@component/Message/MessageChat";
import { observer } from "mobx-react-use-autorun";
import { style } from "typestyle";
import MessageUnlimitedAutoSizer from "@component/Message/MessageUnlimitedAutoSizer";

const container = style({
    width: "100%",
    height: "100%",
    flex: "1 1 auto",
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    paddingLeft: "1em",
    paddingRight: "1em",
    paddingTop: "1em",
});

export default observer(() => {

    return <div className={container} >
        <MessageUnlimitedAutoSizer />
        <MessageChat />
    </div>
})