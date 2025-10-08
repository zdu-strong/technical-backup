import { FriendshipModel } from "@model/FriendshipModel";
import { Button } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { MessageService } from "@common/MessageService";
import api from "@api";
import { FormattedMessage } from "react-intl";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faSpinner } from "@fortawesome/free-solid-svg-icons";

type Props = {
    friendship: FriendshipModel;
    refreshFriendshipList: () => Promise<void>;
}

export default observer((props: Props) => {

    const state = useMobxState({
        loading: false
    });

    async function addToFriendList() {
        if (state.loading) {
            return;
        }
        try {
            state.loading = true;
            await api.Friendship.addToFriendList(props.friendship.friend?.id!);
            await props.refreshFriendshipList();
        } catch (error) {
            MessageService.error(error);
        } finally {
            state.loading = false;
        }
    }

    return <div className="flex flex-row justify-between items-center">
        <div className="flex flex-row">
            {props.friendship.friend?.username}
        </div>
        <Button
            variant="contained"
            style={{
                marginRight: "1em",
            }}
            startIcon={<FontAwesomeIcon icon={state.loading ? faSpinner : faPlus} spin={state.loading} />}
            onClick={addToFriendList}
        >
            <FormattedMessage id="AddToFriends" defaultMessage="Add to friends" />
        </Button>
    </div>;
})