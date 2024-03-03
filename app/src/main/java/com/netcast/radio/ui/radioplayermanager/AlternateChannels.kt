package com.netcast.radio.ui.radioplayermanager

import com.netcast.radio.ui.radio.data.temp.RadioLists

data class AlternateChannels(
    val all: List<RadioLists>,
    val total: Long,
)