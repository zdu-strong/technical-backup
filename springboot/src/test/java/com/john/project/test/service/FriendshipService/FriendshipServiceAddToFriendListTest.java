package com.john.project.test.service.FriendshipService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.john.project.model.UserModel;
import com.john.project.test.common.BaseTest.BaseTest;

public class FriendshipServiceAddToFriendListTest extends BaseTest {
    private UserModel user;
    private UserModel friend;

    @Test
    public void test() {
        this.friendshipService.addToFriendList(this.user.getId(), this.friend.getId());
        var friendshipModel = this.friendshipService.getFriendship(this.user.getId(),
                this.friend.getId());
        assertEquals(this.user.getId(), friendshipModel.getUser().getId());
        assertEquals(this.friend.getId(), friendshipModel.getFriend().getId());
        assertTrue(friendshipModel.getHasInitiative());
        assertTrue(friendshipModel.getIsFriend());
        assertFalse(friendshipModel.getIsInBlacklist());
        assertFalse(friendshipModel.getIsFriendOfFriend());
        assertFalse(friendshipModel.getIsInBlacklistOfFriend());
    }

    @BeforeEach
    public void beforeEach() {
        var userEmail = this.uuidUtil.v4() + "zdu.strong@gmail.com";
        var friendEmail = this.uuidUtil.v4() + "zdu.strong@gmail.com";
        this.user = this.createAccount(userEmail);
        this.friend = this.createAccount(friendEmail);
    }

}
