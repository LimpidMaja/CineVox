/**
 * SelectFriendsActivity.java
 *
 * 10.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.limpidgreen.cinevox.model.RatingSystem;
import com.limpidgreen.cinevox.model.VotingRange;
import com.limpidgreen.cinevox.util.Constants;

/**
 * Select Friends Activity.
 *
 * @author MajaDobnik
 *
 */
public class EditVotingActivity extends Activity {
    /** User Account */
    private Account mAccount;
    /** Account manager */
    private AccountManager mAccountManager;
    /** User Account API Token */
    private String mAuthToken;

    private CheckBox mUseVotingCheck;
    private TextView mTimeLimit;
    private TextView mVotingPercent;
    private TextView mSytemRating;
    private TextView mSystemKnockout;
    private TextView mVotesPerUser;
    private TextView mVotingRange15;
    private TextView mVotingRange10;
    private TextView mTieKnockout;
    private TextView mTieRandom;
    private TextView mKnockoutRounds;
    private TextView mRandomMsg;
    private LinearLayout mVotesPerUserLayout;
    private LinearLayout mRangeLayout;
    private LinearLayout mTieLayout;
    private LinearLayout mKnockoutRoundsLayout;
    private LinearLayout mVotingLayout;

    private Boolean useVoting;
    private Integer timeLimit;
    private Integer votingPercent;
    private RatingSystem ratingSystem;
    private VotingRange votingRange;
    private Integer votesPerUser;
    private Boolean tieKnockout;
    private Integer knockoutRounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_voting);

        if (savedInstanceState != null) {
        } else {
            Bundle bundle = getIntent().getExtras();
            useVoting = bundle.getBoolean(Constants.PARAM_USE_VOTING, true);
            timeLimit = bundle.getInt(Constants.PARAM_TIME_LIMIT, 120);
            votingPercent = bundle.getInt(Constants.PARAM_VOTING_PERCENT, 100);
            int ratingSystemId = bundle.getInt(Constants.PARAM_RATING_SYSTEM, 0);
            ratingSystem = RatingSystem.fromInteger(ratingSystemId);
            int votingRangeId = bundle.getInt(Constants.PARAM_VOTING_RANGE, 0);
            votingRange = VotingRange.fromInteger(votingRangeId);
            votesPerUser = bundle.getInt(Constants.PARAM_USE_VOTES_PER_USER, 2);
            tieKnockout = bundle.getBoolean(Constants.PARAM_TIE_KNOCKOUT, true);
            knockoutRounds = bundle.getInt(Constants.PARAM_KNOCKOUT_ROUNDS, 4);

        } // end if-else

        mUseVotingCheck = (CheckBox) findViewById(R.id.use_voting_check);
        mTimeLimit = (TextView) findViewById(R.id.time_limit);
        mVotingPercent = (TextView) findViewById(R.id.voting_percent);
        mSytemRating = (TextView) findViewById(R.id.system_rating);
        mSystemKnockout = (TextView) findViewById(R.id.system_knockout);
        mVotesPerUser = (TextView) findViewById(R.id.votes_per_user);
        mVotingRange15 = (TextView) findViewById(R.id.voting_range_15);
        mVotingRange10 = (TextView) findViewById(R.id.voting_range_10);
        mTieKnockout = (TextView) findViewById(R.id.tie_knockout);
        mTieRandom = (TextView) findViewById(R.id.tie_random);
        mKnockoutRounds = (TextView) findViewById(R.id.knockout_rounds);
        mRandomMsg = (TextView) findViewById(R.id.random_msg);
        mVotesPerUserLayout = (LinearLayout) findViewById(R.id.votes_per_user_layout);
        mRangeLayout = (LinearLayout) findViewById(R.id.range_Layout);
        mTieLayout = (LinearLayout) findViewById(R.id.tie_layout);
        mKnockoutRoundsLayout = (LinearLayout) findViewById(R.id.knockout_rounds_layout);
        mVotingLayout = (LinearLayout) findViewById(R.id.voting_layout);

        mUseVotingCheck.setChecked(useVoting);
        if (useVoting) {
            mVotingLayout.setVisibility(View.VISIBLE);
            mRandomMsg.setVisibility(View.GONE);
        } else {
            mVotingLayout.setVisibility(View.GONE);
            mRandomMsg.setVisibility(View.VISIBLE);
        } // end if-else
        mUseVotingCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mVotingLayout.setVisibility(View.VISIBLE);
                    mRandomMsg.setVisibility(View.GONE);
                    useVoting = true;
                } else {
                    mVotingLayout.setVisibility(View.GONE);
                    mRandomMsg.setVisibility(View.VISIBLE);
                    useVoting = false;
                }
            }
        });

        mTimeLimit.setText(timeLimit.toString());
        mVotingPercent.setText(votingPercent.toString());
        mVotesPerUser.setText(votesPerUser.toString());
        mKnockoutRounds.setText(knockoutRounds.toString());

        switch (ratingSystem) {
            case VOTING:
                mSytemRating.setBackgroundColor(Color.parseColor("#ffa172f1"));
                mSystemKnockout.setBackgroundColor(Color.parseColor("#ff877ac3"));

                mVotesPerUserLayout.setVisibility(View.VISIBLE);
                mRangeLayout.setVisibility(View.VISIBLE);
                mTieLayout.setVisibility(View.VISIBLE);
                break;
            case KNOCKOUT:
                mSystemKnockout.setBackgroundColor(Color.parseColor("#ffa172f1"));
                mSytemRating.setBackgroundColor(Color.parseColor("#ff877ac3"));

                mKnockoutRoundsLayout.setVisibility(View.VISIBLE);
                mVotesPerUserLayout.setVisibility(View.GONE);
                mRangeLayout.setVisibility(View.GONE);
                mTieLayout.setVisibility(View.GONE);
                break;
        }
        mSytemRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSytemRating.setBackgroundColor(Color.parseColor("#ffa172f1"));
                mSystemKnockout.setBackgroundColor(Color.parseColor("#ff877ac3"));

                mVotesPerUserLayout.setVisibility(View.VISIBLE);
                mRangeLayout.setVisibility(View.VISIBLE);
                mTieLayout.setVisibility(View.VISIBLE);
                if (tieKnockout) {
                    mKnockoutRoundsLayout.setVisibility(View.VISIBLE);
                } else {
                    mKnockoutRoundsLayout.setVisibility(View.GONE);
                }
                ratingSystem = RatingSystem.VOTING;
            }
        });

        mSystemKnockout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSystemKnockout.setBackgroundColor(Color.parseColor("#ffa172f1"));
                mSytemRating.setBackgroundColor(Color.parseColor("#ff877ac3"));

                mVotesPerUserLayout.setVisibility(View.GONE);
                mRangeLayout.setVisibility(View.GONE);
                mTieLayout.setVisibility(View.GONE);
                mKnockoutRoundsLayout.setVisibility(View.VISIBLE);
                ratingSystem = RatingSystem.KNOCKOUT;
            }
        });

        switch (votingRange) {
            case ONE_TO_FIVE:
                mVotingRange15.setBackgroundColor(Color.parseColor("#ffa172f1"));
                mVotingRange10.setBackgroundColor(Color.parseColor("#ff877ac3"));
                mVotesPerUserLayout.setVisibility(View.VISIBLE);
                break;
            case ONE_TO_TEN:
                mVotingRange10.setBackgroundColor(Color.parseColor("#ffa172f1"));
                mVotingRange15.setBackgroundColor(Color.parseColor("#ff877ac3"));
                mVotesPerUserLayout.setVisibility(View.GONE);
                break;
        }
        mVotingRange15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVotingRange15.setBackgroundColor(Color.parseColor("#ffa172f1"));
                mVotingRange10.setBackgroundColor(Color.parseColor("#ff877ac3"));
                mVotesPerUserLayout.setVisibility(View.VISIBLE);
                votingRange = VotingRange.ONE_TO_FIVE;
            }
        });

        mVotingRange10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVotingRange10.setBackgroundColor(Color.parseColor("#ffa172f1"));
                mVotingRange15.setBackgroundColor(Color.parseColor("#ff877ac3"));
                mVotesPerUserLayout.setVisibility(View.GONE);
                votingRange = VotingRange.ONE_TO_TEN;
            }
        });

        if (tieKnockout) {
            mTieKnockout.setBackgroundColor(Color.parseColor("#ffa172f1"));
            mTieRandom.setBackgroundColor(Color.parseColor("#ff877ac3"));
            mKnockoutRoundsLayout.setVisibility(View.VISIBLE);
        } else {
            mTieRandom.setBackgroundColor(Color.parseColor("#ffa172f1"));
            mTieKnockout.setBackgroundColor(Color.parseColor("#ff877ac3"));
            mKnockoutRoundsLayout.setVisibility(View.GONE);
        }

        mTieKnockout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTieKnockout.setBackgroundColor(Color.parseColor("#ffa172f1"));
                mTieRandom.setBackgroundColor(Color.parseColor("#ff877ac3"));
                mKnockoutRoundsLayout.setVisibility(View.VISIBLE);
                tieKnockout = true;
            }
        });

        mTieRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTieRandom.setBackgroundColor(Color.parseColor("#ffa172f1"));
                mTieKnockout.setBackgroundColor(Color.parseColor("#ff877ac3"));
                mKnockoutRoundsLayout.setVisibility(View.GONE);
                tieKnockout = false;
            }
        });

    }

    /**
     * Handle Done in Edit Rating.
     *
     * @param v view
     */
    public void handleDoneClick(View v) {
        Intent intent = new Intent();

        if (mUseVotingCheck.isChecked() && (mTimeLimit.getText().toString().trim().isEmpty() ||
                mVotingPercent.getText().toString().trim().isEmpty())) {
            Toast toast = Toast.makeText(this,
                    "Missing fields!",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (mUseVotingCheck.isChecked()) {

            timeLimit = Integer.valueOf(mTimeLimit.getText().toString().trim());
            votingPercent = Integer.valueOf(mVotingPercent.getText().toString().trim());
            intent.putExtra(Constants.PARAM_TIME_LIMIT, timeLimit);
            intent.putExtra(Constants.PARAM_VOTING_PERCENT, votingPercent);

            if (RatingSystem.VOTING.equals(ratingSystem)) {
                if (mVotesPerUser.getText().toString().trim().isEmpty()) {
                    Toast toast = Toast.makeText(this,
                            "Missing fields!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                votesPerUser = Integer.valueOf(mVotesPerUser.getText().toString().trim());
                intent.putExtra(Constants.PARAM_USE_VOTES_PER_USER, votesPerUser);
            } else {
                intent.putExtra(Constants.PARAM_USE_VOTES_PER_USER, 0);
            }

            if (tieKnockout || RatingSystem.KNOCKOUT.equals(ratingSystem)) {
                if (mKnockoutRounds.getText().toString().trim().isEmpty()) {
                    Toast toast = Toast.makeText(this,
                            "Missing fields!",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                knockoutRounds = Integer.valueOf(mKnockoutRounds.getText().toString().trim());
                intent.putExtra(Constants.PARAM_KNOCKOUT_ROUNDS, knockoutRounds);
                intent.putExtra(Constants.PARAM_TIE_KNOCKOUT, true);
            } else {
                intent.putExtra(Constants.PARAM_TIE_KNOCKOUT, false);
                intent.putExtra(Constants.PARAM_KNOCKOUT_ROUNDS, 0);
            }

            intent.putExtra(Constants.PARAM_USE_VOTING, useVoting);
            intent.putExtra(Constants.PARAM_RATING_SYSTEM, ratingSystem.ordinal());
            intent.putExtra(Constants.PARAM_VOTING_RANGE, votingRange.ordinal());
        } else if (!mUseVotingCheck.isChecked()) {
            intent.putExtra(Constants.PARAM_USE_VOTING, useVoting);

            ratingSystem = RatingSystem.RANDOM;
            intent.putExtra(Constants.PARAM_RATING_SYSTEM, ratingSystem.ordinal());

            intent.putExtra(Constants.PARAM_TIME_LIMIT, 1);
            intent.putExtra(Constants.PARAM_VOTING_PERCENT, 100);
            intent.putExtra(Constants.PARAM_USE_VOTES_PER_USER, 0);
            intent.putExtra(Constants.PARAM_KNOCKOUT_ROUNDS, 0);
            intent.putExtra(Constants.PARAM_VOTING_RANGE, votingRange.ordinal());
            intent.putExtra(Constants.PARAM_TIE_KNOCKOUT, false);
        }

        setResult(RESULT_OK, intent);
        finish();
    }
}
