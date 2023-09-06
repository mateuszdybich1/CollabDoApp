package com.dybich.collabdoapp

enum class TaskStatus(val value: Int) {
    Undone(0),
    Started(1),
    WaitingForApprovement(2),
    Done(3)
}