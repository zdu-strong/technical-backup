import { observer, useMobxState } from "mobx-react-use-autorun";
import { Button } from "@mui/material";
import api from "@/api";
import { MessageService } from "@/common/MessageService";
import { FormattedMessage } from "react-intl";
import { faSpinner, faDownload, faTrashCan, faArrowRotateLeft } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { UserMessageModel } from "@/model/UserMessageModel";
import path from "path";
import { GlobalUserInfo } from "@/common/Server";

export default observer((props: {
  message: UserMessageModel
}) => {

  const state = useMobxState({
    loading: false,
  })

  async function withdrawn() {
    if (state.loading) {
      return;
    }
    try {
      state.loading = true;
      await api.UserMessage.recallMessage(props.message.id);
    } catch (error) {
      MessageService.error(error);
      state.loading = false;
    }
  }

  async function deleteMessage() {
    if (state.loading) {
      return;
    }
    try {
      state.loading = true;
      await api.UserMessage.deleteMessage(props.message.id);
    } catch (error) {
      MessageService.error(error);
      state.loading = false;
    }
  }

  return <div className="flex flex-col"
    style={{
      whiteSpace: "pre-wrap",
      wordBreak: "break-word",
      overflowWrap: "break-word",
    }}
  >
    <div className="flex flex-row justify-between">
      <div className="flex flex-row">
        {props.message.pageNum}
        {":"}
      </div>
      {props.message.user.id === GlobalUserInfo.id && <Button
        variant="outlined"
        onClick={withdrawn}
        style={{ marginRight: "1em" }}
        size="small"
        startIcon={<FontAwesomeIcon icon={state.loading ? faSpinner : faArrowRotateLeft} spin={state.loading} style={{ fontSize: "small" }} />}
      >
        <FormattedMessage id="Withdrawn" defaultMessage="Withdrawn" />
      </Button>}
      {props.message.user.id !== GlobalUserInfo.id && <Button
        variant="outlined"
        onClick={deleteMessage}
        style={{ marginRight: "1em" }}
        size="small"
        startIcon={<FontAwesomeIcon icon={state.loading ? faSpinner : faTrashCan} spin={state.loading} style={{ fontSize: "small" }} />}
      >
        <FormattedMessage id="Delete" defaultMessage="Delete" />
      </Button>}
    </div>
    {!!props.message.url && <div>
      <Button
        variant="contained"
        startIcon={<FontAwesomeIcon icon={faDownload} />}
        href={props.message.url}
        download={true}
      >
        {decodeURIComponent(path.basename(props.message.url))}
      </Button>
    </div>}
    {!props.message.url && <div>
      {props.message.content}
    </div>}
  </div>
})
