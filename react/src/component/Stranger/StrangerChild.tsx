import { FriendshipModel } from "@model/FriendshipModel";
import { Button } from "@mui/material";
import { observer } from "mobx-react-use-autorun";
import api from "@api";
import { FormattedMessage } from "react-intl";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faSpinner } from "@fortawesome/free-solid-svg-icons";
import { useMultipleSubmit } from "@/common/use-hook";

type Props = {
    friendship: FriendshipModel;
    refreshFriendshipList: () => Promise<void>;
}

export default observer((props: Props) => {

    const addToFriendList = useMultipleSubmit(async function () {
        await api.Friendship.addToFriendList(props.friendship.friend?.id!);
        await props.refreshFriendshipList();
    });

    return <div className="flex flex-row justify-between items-center">
        <div className="flex flex-row">
            {props.friendship.friend?.username}
        </div>
        <Button
            variant="contained"
            style={{
                marginRight: "1em",
            }}
            startIcon={<FontAwesomeIcon icon={addToFriendList.loading ? faSpinner : faPlus} spin={addToFriendList.loading} />}
            onClick={addToFriendList.resubmit}
        >
            <FormattedMessage id="AddToFriends" defaultMessage="Add to friends" />
        </Button>
    </div>;
})