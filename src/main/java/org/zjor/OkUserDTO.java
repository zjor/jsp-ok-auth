package org.zjor;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author: Sergey Royz
 * @since: 21.10.2013
 */
@Data
public class OkUserDTO {

    @SerializedName("uid")
    private String userId;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("name")
    private String fullName;

    @SerializedName("gender")
    private String gender;

    @SerializedName("age")
    private Integer age;

    @SerializedName("birthday")
    private String birthDate;

    @SerializedName("pic_1")
    private String smallImageURL;

    @SerializedName("pic_2")
    private String largeImageURL;
}
