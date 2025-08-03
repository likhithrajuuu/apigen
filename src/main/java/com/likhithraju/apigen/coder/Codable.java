package com.likhithraju.apigen.coder;

import com.likhithraju.apigen.configs.ApiConfigModel;

public interface Codable {
	public String toCode(ApiConfigModel configuration) throws CoderException;
}
