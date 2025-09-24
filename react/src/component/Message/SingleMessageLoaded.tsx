import { observer, useMobxState } from "mobx-react-use-autorun";
import { useGlobalSingleMessage } from "@component/Message/js/Global_Chat";
import LoadingOrErrorComponent from "@common/MessageService/LoadingOrErrorComponent";
import SingleMessage from "@/component/Message/SingleMessage";

type Props = {
  pageNum: number
}

export default observer((props: Props) => {

  const state = useMobxState({
  }, {
    ...useGlobalSingleMessage(props.pageNum)
  })

  return <LoadingOrErrorComponent ready={state.ready} error={null}>
    <SingleMessage message={state.message} key={state.message.id} />
  </LoadingOrErrorComponent>
})
