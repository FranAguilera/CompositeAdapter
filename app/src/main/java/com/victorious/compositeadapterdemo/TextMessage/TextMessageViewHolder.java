/*
 * Copyright 2017 Victorious,Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.victorious.compositeadapterdemo.TextMessage;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.victorious.compositeadapterdemo.R;

public class TextMessageViewHolder extends RecyclerView.ViewHolder {

    private TextView textMessageView;

    public TextMessageViewHolder(View itemView) {
        super(itemView);
        textMessageView = (TextView) itemView.findViewById(R.id.message);
    }

    public void bind(TextMessage textMessage) {
        textMessageView.setText(textMessage.getBody());
    }
}
