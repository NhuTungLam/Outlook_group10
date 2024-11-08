package vn.edu.usth.outlook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.usth.outlook.Email_receiver;
import vn.edu.usth.outlook.R;
import vn.edu.usth.outlook.listener.SelectListener;

public class ReceiveAdapter extends RecyclerView.Adapter<ReceiveAdapter.CustomViewHolder> {
    private Context context;
    private List<Email_receiver> list;
    private final SelectListener listener;

    public ReceiveAdapter(Context context, List<Email_receiver> list, SelectListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_inbox, parent, false);
        return new CustomViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        // Gán giá trị cho các view trong item layout dựa trên vị trí
        Email_receiver email = list.get(position);
        holder.textName.setText(email.getReceiver());
        holder.textHeadmail.setText(email.getSubject());
        holder.textContent.setText(email.getContent());
        // Nếu có các thuộc tính khác cần hiển thị, hãy thêm vào đây
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filterList(List<Email_receiver> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return list == null || list.isEmpty();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView textName, textHeadmail, textContent, textDate;
        public ImageView imageView;
        public CardView cardView;

        public CustomViewHolder(@NonNull View itemView, SelectListener listener) {
            super(itemView);
//            textName = itemView.findViewById(R.id.nameReceive);
//            textHeadmail = itemView.findViewById(R.id.head_emailReceive);
//            textContent = itemView.findViewById(R.id.contentReceive);
//            imageView = itemView.findViewById(R.id.imageviewReceive);
//            cardView = itemView.findViewById(R.id.main_containerReceive);
//            textDate = itemView.findViewById(R.id.dateReceive);

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onItemClicked(pos);
                    }
                }
            });
        }
    }
}
