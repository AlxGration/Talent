package com.team.alex.talent.presentation.questions

import com.team.alex.talent.R

class FragmentState (
    val isSelectionModeOn: Boolean = false,
    val isAbleToDeleteItems: Boolean = true,
    val isNavBarVisible: Boolean = true,
    val isAppBarChanged: Boolean = false,
    val menuRes: Int = R.menu.questions_app_bar_menu,
    val actionNavToQuestionDetails: Int = R.id.action_navigation_questions_to_question_details,
    val appBarTitleRes: Int = R.string.questions,
)