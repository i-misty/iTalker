package com.imist.italker.factory.data.group;

import com.imist.italker.factory.model.card.GroupCard;
import com.imist.italker.factory.model.card.GroupMemberCard;

public interface GroupCenter {

    //群卡片的处理
    void dispatch(GroupCard...cards);
    //群成员的处理
    void dispatch(GroupMemberCard ...cards);
}
