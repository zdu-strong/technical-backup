import { FriendshipModel } from "@model/FriendshipModel";
import { observer, useMobxState } from "mobx-react-use-autorun";
import StrangerChild from "@component/Stranger/StrangerChild";
import api from '@api'
import LoadingOrErrorComponent from "@common/MessageService/LoadingOrErrorComponent";
import { FormattedMessage } from "react-intl";
import { useMultipleQuery } from "@/common/use-hook";

export default observer(() => {

    const state = useMobxState({
        friendshipList: [] as FriendshipModel[],
    })

    const getFriendshipList = useMultipleQuery(async function () {
        await refreshFriendshipList();
    });

    async function refreshFriendshipList() {
        const { items: list } = await api.Friendship.getStrangerList();
        state.friendshipList = list;
    }

    return <div className="flex flex-col flex-auto">
        <LoadingOrErrorComponent ready={getFriendshipList.ready} error={getFriendshipList.error}>
            <div>
                <FormattedMessage id="Stranger" defaultMessage="Stranger" />
            </div>
            {state.friendshipList.map(item => <StrangerChild
                friendship={item}
                key={item.id}
                refreshFriendshipList={refreshFriendshipList}
            />)}
        </LoadingOrErrorComponent>
    </div>;
})