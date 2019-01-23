package com.imist.italker.push.frags.main;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imist.italker.common.app.Fragment;
import com.imist.italker.common.app.PresenterFragment;
import com.imist.italker.common.widget.EmptyView;
import com.imist.italker.common.widget.GalleryView;
import com.imist.italker.common.widget.PortraitView;
import com.imist.italker.common.widget.recycler.RecyclerAdapter;
import com.imist.italker.face.Face;
import com.imist.italker.factory.model.db.Session;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.presenter.message.SessionContract;
import com.imist.italker.factory.presenter.message.SessionPresenter;
import com.imist.italker.push.R;
import com.imist.italker.push.activities.MessageActivity;
import com.imist.italker.push.activities.PersonalActivity;
import com.imist.italker.utils.DateTimeUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class ActiveFragment extends PresenterFragment<SessionContract.Presenter>
        implements SessionContract.View {
    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    //适配器User，可以直接从数据库查询数据
    private RecyclerAdapter<Session> mAdapter;

    public ActiveFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_active;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        // 初始化Recycler
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        /**
         * 这里的DBflow一定得是 api引入，可传递的，不然该调用模块无法识别
         * 继承了BaseModel 的数据库实体类导致编译不通过，原因就在于DBflow也是编译时注解，
         * 一定得注意项目权限的问题；因此在使用编译时注解的时候一定得注意
         */
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<Session>() {
            @Override
            protected int getItemViewType(int position, Session session) {
                return R.layout.cell_chat_list;
            }

            @Override
            protected ViewHolder<Session> onCreateViewHolder(View root, int viewType) {
                return new ActiveFragment.ViewHolder(root);
            }
        });

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Session>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Session session) {
                MessageActivity.show(getContext(), session);
            }
        });
        // 初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);

    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        //进行一次数据加载
        mPresenter.start();
    }

    @Override
    protected SessionContract.Presenter initPresenter() {
        return new SessionPresenter(this);
    }

    @Override
    public RecyclerAdapter<Session> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    /**
     * ViewHolder 这里不可以私有，因为butterknife 是编译时注解，所以需要具备包访问权限；
     */
    class ViewHolder extends RecyclerAdapter.ViewHolder<Session> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_content)
        TextView mContent;

        @BindView(R.id.txt_time)
        TextView mTime;


        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Session session) {
            //Glide.with(ContactFragment.this)绑定Fragment,随生命周期影响优化加载
            mPortraitView.setup(Glide.with(ActiveFragment.this), session.getPicture());
            mName.setText(session.getTitle());

            String str = TextUtils.isEmpty(session.getContent()) ? "" : session.getContent();
            Spannable spannable = new SpannableString(str);
            // 解析表情
            Face.decode(mContent, spannable, (int) mContent.getTextSize());
            // 把内容设置到布局上
            mContent.setText(spannable);

            mTime.setText(DateTimeUtil.getSimpleDate(session.getModifyAt()));
        }
    }
}
