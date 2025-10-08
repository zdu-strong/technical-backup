import { FriendshipModel } from "@model/FriendshipModel";
import { Button } from "@mui/material";
import { observer, useMobxState } from "mobx-react-use-autorun";
import api from "@api";
import { MessageService } from "@common/MessageService";
import { FormattedMessage } from "react-intl";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner, faTrash } from "@fortawesome/free-solid-svg-icons";

type Props = {
    friendship: FriendshipModel;
    refreshFriendList: () => Promise<void>;

}

export default observer((props: Props) => {

    const state = useMobxState({
        loading: false,
    });


    async function deleteFromFriendList() {
        if (state.loading) {
            return;
        }
        try {
            state.loading = true;
            await api.Friendship.deleteFromFriendList(props.friendship.friend?.id!)
            await props.refreshFriendList();
        } catch (error) {
            MessageService.error(error)
        } finally {
            state.loading = false
        }
    }

    return <div className="flex flex-row justify-between">
        <div>
            {props.friendship.friend?.username}
        </div>
        <Button
            variant="contained"
            style={{
                marginRight: "1em",
            }}
            startIcon={<FontAwesomeIcon icon={state.loading ? faSpinner : faTrash} spin={state.loading} />}
            onClick={deleteFromFriendList}
        >
            <FormattedMessage id="LiftYourFriends" defaultMessage="Lift your friends" />
        </Button>
    </div>;
})