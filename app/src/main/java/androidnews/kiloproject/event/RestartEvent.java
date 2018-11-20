package androidnews.kiloproject.event;

import android.os.Message;

public class RestartEvent {
  private String message;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
