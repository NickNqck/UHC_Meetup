package fr.nicknqck.utils.discord;

import lombok.Getter;
import lombok.NonNull;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.*;
import java.util.List;

public final class WebhookBuilder {
    @NonNull
    private final String url;
    @NonNull
    private String content;
    @NonNull
    private String username;
    private String avatarUrl;
    private boolean tts;
    private final List<EmbedObject> embeds = new ArrayList<>();

    private WebhookBuilder(@NonNull String url) {
        this.url = url;
        content = "";
        username = "";
    }

    public static WebhookBuilder newBuilder(@NonNull String url) {
        return new WebhookBuilder(url);
    }

    public WebhookBuilder content(String content) {
        this.content = content;
        return this;
    }
    public WebhookBuilder content(@NonNull String... content) {
        StringBuilder origin = new StringBuilder(this.content);
        for (String s : content) {
            origin.append(s).append(" ");
        }
        this.content = origin.toString();
        return this;
    }
    public WebhookBuilder username(String username) {
        this.username = username;
        return this;
    }

    public WebhookBuilder avatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public WebhookBuilder tts(boolean tts) {
        this.tts = tts;
        return this;
    }

    public WebhookBuilder addEmbed(EmbedObject embed) {
        this.embeds.add(embed);
        return this;
    }

    public Webhook build() {
        return new Webhook(this);
    }

    public void buildAndExecute() throws IOException {
        build().execute();
    }

    public static class Webhook {

        private final String url;
        private final String content;
        private final String username;
        private final String avatarUrl;
        private final boolean tts;
        private final List<EmbedObject> embeds;

        private Webhook(WebhookBuilder builder) {
            this.url = builder.url;
            this.content = builder.content;
            this.username = builder.username;
            this.avatarUrl = builder.avatarUrl;
            this.tts = builder.tts;
            this.embeds = builder.embeds;
        }

        public void addEmbed(EmbedObject embed) {
            this.embeds.add(embed);
        }

        public void execute() throws IOException {
            if (this.content == null && this.embeds.isEmpty()) {
                throw new IllegalArgumentException("Set content or add at least one EmbedObject");
            }

            final JSONObject json = new JSONObject();

            json.put("content", this.content);
            json.put("username", this.username);
            json.put("avatar_url", this.avatarUrl);
            json.put("tts", this.tts);

            if (!this.embeds.isEmpty()) {
                final List<JSONObject> embedObjects = new ArrayList<>();

                for (EmbedObject embed : this.embeds) {
                    JSONObject jsonEmbed = new JSONObject();

                    jsonEmbed.put("title", embed.getTitle());
                    jsonEmbed.put("description", embed.getDescription());
                    jsonEmbed.put("url", embed.getUrl());

                    if (embed.getColor() != null) {
                        Color color = embed.getColor();
                        int rgb = color.getRed();
                        rgb = (rgb << 8) + color.getGreen();
                        rgb = (rgb << 8) + color.getBlue();

                        jsonEmbed.put("color", rgb);
                    }

                    EmbedObject.Footer footer = embed.getFooter();
                    EmbedObject.Image image = embed.getImage();
                    EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
                    EmbedObject.Author author = embed.getAuthor();
                    List<EmbedObject.Field> fields = embed.getFields();

                    if (footer != null) {
                        JSONObject jsonFooter = new JSONObject();

                        jsonFooter.put("text", footer.getText());
                        jsonFooter.put("icon_url", footer.getIconUrl());
                        jsonEmbed.put("footer", jsonFooter);
                    }

                    if (image != null) {
                        JSONObject jsonImage = new JSONObject();

                        jsonImage.put("url", image.getUrl());
                        jsonEmbed.put("image", jsonImage);
                    }

                    if (thumbnail != null) {
                        JSONObject jsonThumbnail = new JSONObject();

                        jsonThumbnail.put("url", thumbnail.getUrl());
                        jsonEmbed.put("thumbnail", jsonThumbnail);
                    }

                    if (author != null) {
                        JSONObject jsonAuthor = new JSONObject();

                        jsonAuthor.put("name", author.getName());
                        jsonAuthor.put("url", author.getUrl());
                        jsonAuthor.put("icon_url", author.getIconUrl());
                        jsonEmbed.put("author", jsonAuthor);
                    }

                    List<JSONObject> jsonFields = new ArrayList<>();
                    for (EmbedObject.Field field : fields) {
                        JSONObject jsonField = new JSONObject();

                        jsonField.put("name", field.getName());
                        jsonField.put("value", field.getValue());
                        jsonField.put("inline", field.isInline());

                        jsonFields.add(jsonField);
                    }

                    jsonEmbed.put("fields", jsonFields.toArray());
                    embedObjects.add(jsonEmbed);
                }

                json.put("embeds", embedObjects.toArray());
            }

            URL url = new URL(this.url);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("User-Agent", "Value");
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStream stream = connection.getOutputStream();
            stream.write(json.toString().getBytes());
            stream.flush();
            stream.close();

            connection.getInputStream().close();
            connection.disconnect();
        }
    }

    @Getter
    public static class EmbedObject {
        private String title;
        private String description;
        private String url;
        private Color color;

        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;
        private final List<Field> fields = new ArrayList<>();

        public EmbedObject setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedObject setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedObject setUrl(String url) {
            this.url = url;
            return this;
        }

        public EmbedObject setColor(Color color) {
            this.color = color;
            return this;
        }

        public EmbedObject setFooter(String text, String icon) {
            this.footer = new Footer(text, icon);
            return this;
        }

        public EmbedObject setThumbnail(String url) {
            this.thumbnail = new Thumbnail(url);
            return this;
        }

        public EmbedObject setImage(String url) {
            this.image = new Image(url);
            return this;
        }

        public EmbedObject setAuthor(String name, String url, String icon) {
            this.author = new Author(name, url, icon);
            return this;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        private static class Footer {
            private final String text;
            private final String iconUrl;

            private Footer(String text, String iconUrl) {
                this.text = text;
                this.iconUrl = iconUrl;
            }

            private String getText() {
                return text;
            }

            private String getIconUrl() {
                return iconUrl;
            }
        }

        private static class Thumbnail {
            private final String url;

            private Thumbnail(String url) {
                this.url = url;
            }

            private String getUrl() {
                return url;
            }
        }

        private static class Image {
            private final String url;

            private Image(String url) {
                this.url = url;
            }

            private String getUrl() {
                return url;
            }
        }

        private static class Author {
            private final String name;
            private final String url;
            private final String iconUrl;

            private Author(String name, String url, String iconUrl) {
                this.name = name;
                this.url = url;
                this.iconUrl = iconUrl;
            }

            private String getName() {
                return name;
            }

            private String getUrl() {
                return url;
            }

            private String getIconUrl() {
                return iconUrl;
            }
        }

        private static class Field {
            private final String name;
            private final String value;
            private final boolean inline;

            private Field(String name, String value, boolean inline) {
                this.name = name;
                this.value = value;
                this.inline = inline;
            }

            private String getName() {
                return name;
            }

            private String getValue() {
                return value;
            }

            private boolean isInline() {
                return inline;
            }
        }
    }

    private static class JSONObject {

        private final HashMap<String, Object> map = new HashMap<>();

        void put(String key, Object value) {
            if (value != null) {
                map.put(key, value);
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            Set<Map.Entry<String, Object>> entrySet = map.entrySet();
            builder.append("{");

            int i = 0;
            for (Map.Entry<String, Object> entry : entrySet) {
                Object val = entry.getValue();
                builder.append(quote(entry.getKey())).append(":");

                if (val instanceof String) {
                    builder.append(quote(String.valueOf(val)));
                } else if (val instanceof Integer) {
                    builder.append(Integer.valueOf(String.valueOf(val)));
                } else if (val instanceof Boolean) {
                    builder.append(val);
                } else if (val instanceof JSONObject) {
                    builder.append(val.toString());
                } else if (val.getClass().isArray()) {
                    builder.append("[");
                    int len = Array.getLength(val);
                    for (int j = 0; j < len; j++) {
                        builder.append(Array.get(val, j).toString()).append(j != len - 1 ? "," : "");
                    }
                    builder.append("]");
                }

                builder.append(++i == entrySet.size() ? "}" : ",");
            }

            return builder.toString();
        }

        private String quote(String string) {
            return "\"" + string + "\"";
        }
    }
}