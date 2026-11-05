import { observer } from "mobx-react-use-autorun";
import { useGlobalSingleMessage } from "@/component/message/js/Global_Chat";
import LoadingOrErrorComponent from "@common/MessageService/LoadingOrErrorComponent";
import SingleMessage from "@/component/message/SingleMessage";

type Props = {
    pageNum: number
}

export default observer((props: Props) => {

    const { ready, message } = useGlobalSingleMessage(props.pageNum);

    return <LoadingOrErrorComponent ready={ready} error={null}>
        <SingleMessage message={message} key={message.id} />
    </LoadingOrErrorComponent>
})
