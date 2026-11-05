import { FriendshipModel } from "@model/FriendshipModel";
import { Button } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import api from "@api";
import { MessageService } from "@common/MessageService";
import { FormattedMessage } from "react-intl";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner, faTrash } from "@fortawesome/free-solid-svg-icons";
import { useMultipleSubmit } from "@/common/use-hook";

type Props = {
    friendship: FriendshipModel;
    refreshFriendList: () => Promise<void>;

}

export default observer((props: Props) => {

    const deleteFromFriendList = useMultipleSubmit(async function () {
        await api.Friendship.deleteFromFriendList(props.friendship.friend?.id!)
        await props.refreshFriendList();
    });

    return <div className="flex flex-row justify-between">
        <div>
            {props.friendship.friend?.username}
        </div>
        <Button
            variant="contained"
            style={{
                marginRight: "1em",
            }}
            startIcon={<FontAwesomeIcon icon={deleteFromFriendList.loading ? faSpinner : faTrash} spin={deleteFromFriendList.loading} />}
            onClick={deleteFromFriendList.resubmit}
        >
            <FormattedMessage id="LiftYourFriends" defaultMessage="Lift your friends" />
        </Button>
    </div>;
})