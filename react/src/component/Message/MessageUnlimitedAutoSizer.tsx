import { observer } from "mobx-react-use-autorun";
import { style } from "typestyle";
import { AutoSizer } from 'react-virtualized';
import MessageUnlimitedList from '@component/Message/MessageUnlimitedList';
import { useGlobalMessageReady } from "@component/Message/js/Global_Chat";
import LoadingOrErrorComponent from "@common/MessageService/LoadingOrErrorComponent";

const containerAutoSizer = style({
    display: "flex",
    flex: "1 1 auto",
    flexDirection: "column",
    width: "100%",
});

export default observer(() => {

    const { ready, error } = useGlobalMessageReady();

    return <div className={containerAutoSizer}>
        <LoadingOrErrorComponent ready={ready} error={!ready && error}>
            <AutoSizer>
                {(size) => <MessageUnlimitedList {...size} />}
            </AutoSizer>
        </LoadingOrErrorComponent>
    </div>
})