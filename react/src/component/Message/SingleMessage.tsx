import { observer } from "mobx-react-use-autorun";
import { Button, Chip } from "@mui/material";
import api from "@api";
import { FormattedMessage } from "react-intl";
import { faSpinner, faDownload, faTrashCan, faArrowRotateLeft, faFaceGrin, faFaceFlushed } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { UserMessageModel } from "@model/UserMessageModel";
import path from "path";
import { GlobalUserInfo } from "@common/Server";
import { useOnceSubmit } from "@/common/use-hook";

type Props = {
    message: UserMessageModel
}

export default observer((props: Props) => {

    const withdrawn = useOnceSubmit(async function () {
        await api.UserMessage.recallMessage(props.message.id);
    });

    const deleteMessage = useOnceSubmit(async function () {
        await api.UserMessage.deleteMessage(props.message.id);
    });

    return <div
        className="flex flex-col h-full"
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
                <Chip
                    style={{ marginLeft: "1em" }}
                    icon={<FontAwesomeIcon icon={GlobalUserInfo.id === props.message.user.id ? faFaceGrin : faFaceFlushed} />}
                    size="small"
                    label={props.message.user.username}
                    variant="outlined"
                    color={GlobalUserInfo.id === props.message.user.id ? "secondary" : "primary"}
                />
            </div>
            {props.message.user.id === GlobalUserInfo.id && <Button
                variant="outlined"
                onClick={withdrawn.resubmit}
                style={{ marginRight: "1em" }}
                size="small"
                startIcon={<FontAwesomeIcon icon={withdrawn.loading ? faSpinner : faArrowRotateLeft} spin={withdrawn.loading} style={{ fontSize: "small" }} />}
            >
                <FormattedMessage id="Withdrawn" defaultMessage="Withdrawn" />
            </Button>}
            {props.message.user.id !== GlobalUserInfo.id && <Button
                variant="outlined"
                onClick={deleteMessage.resubmit}
                style={{ marginRight: "1em" }}
                size="small"
                startIcon={<FontAwesomeIcon icon={deleteMessage.loading ? faSpinner : faTrashCan} spin={deleteMessage.loading} style={{ fontSize: "small" }} />}
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
        {!props.message.url && <div className="flex flex-row flex-auto overflow-auto">
            {props.message.content}
        </div>}
    </div>
})
