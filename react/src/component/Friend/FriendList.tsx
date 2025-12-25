import { FriendshipModel } from "@model/FriendshipModel";
import { observer, useMobxState } from "mobx-react-use-autorun";
import { useMount } from "mobx-react-use-autorun";
import FriendChildComponent from "@component/Friend/FriendChild";
import api from '@api'
import LoadingOrErrorComponent from "@common/MessageService/LoadingOrErrorComponent";
import { FormattedMessage } from "react-intl";
import { useMultipleQuery } from "@/common/use-hook";

export default observer(() => {

    const state = useMobxState({
        friendshipList: [] as FriendshipModel[],
    })

    const getFriendList = useMultipleQuery(async () => {
        await refreshFriendList();
    });

    async function refreshFriendList() {
        const { items: list } = await api.Friendship.getFriendList();
        state.friendshipList = list;
    }

    return <div className="flex flex-col flex-auto">
        <LoadingOrErrorComponent ready={getFriendList.ready} error={getFriendList.error}>
            <div>
                <FormattedMessage id="Friends" defaultMessage="Friends" />
            </div>
            {state.friendshipList.map(item => <FriendChildComponent
                friendship={item}
                key={item.id}
                refreshFriendList={refreshFriendList}
            />)}
        </LoadingOrErrorComponent>
    </div>;
})