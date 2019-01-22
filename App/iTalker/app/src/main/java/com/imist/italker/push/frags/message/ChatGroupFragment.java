package com.imist.italker.push.frags.message;


import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.model.db.view.MemberUserModel;
import com.imist.italker.factory.presenter.message.ChatContact;
import com.imist.italker.factory.presenter.message.ChatGroupPresenter;
import com.imist.italker.push.R;
import com.imist.italker.push.activities.PersonalActivity;

import java.util.List;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatGroupFragment extends ChatFragment<Group>
        implements ChatContact.GroupView {


    @BindView(R.id.im_header)
    ImageView mHeader;

    @BindView(R.id.lay_members)
    LinearLayout mLayMembers;

    @BindView(R.id.txt_member_more)
    TextView mMembersMore;

    public ChatGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getHeaderLayoutId() {
        return R.layout.lay_chat_header_group;
    }

    @Override
    protected ChatContact.Presenter initPresenter() {
        return new ChatGroupPresenter(this,mReceiverId);
    }

    @Override
    public void onInit(Group group) {
        mCollapsingLayout.setTitle(group.getName());
        Glide.with(this)
                .load(group.getPicture())
                .centerCrop()
                .placeholder(R.drawable.default_banner_group)
                .into(mHeader);
    }

    @Override
    public void onInitGroupMembers(List<MemberUserModel> members, long moreCount) {
        if (members == null || members.size() == 0){
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (final MemberUserModel member : members) {
            // 添加成员头像；false返回当前空间，true返回父控件;
            ImageView p = (ImageView) inflater.inflate(R.layout.lay_chat_group_portrait,mLayMembers,false);
            mLayMembers.addView(p,0);
            Glide.with(this)
                    .load(member.portrait)
                    .placeholder(R.drawable.default_portrait)
                    .centerCrop()
                    .dontAnimate()//这里不使用动画防止异常
                    .into(p);
            //成员信息查看
            p.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PersonalActivity.show(getContext(),member.userId);
                }
            });
        }
        //更多按钮出现逻辑，若是多余的人数大于0 出现显示成员列表的按钮，否则隐藏
        if (moreCount > 0){
            mMembersMore.setText(String.format("+%s",moreCount));
            mMembersMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //显示成员列表
                }
            });
        }else {
            mMembersMore.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAdminOption(boolean isAdmin) {
        if (isAdmin){
            mToolbar.inflateMenu(R.menu. chat_group);
            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_add){
                        //todo 进行添加群成员操作
                        return true;
                    }
                    return false;
                }
            });
        }
    }



}
