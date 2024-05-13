package vkgen

enum class VkPostfix {

	VALVE,
	QNX,
	QCOM,
	NVX,
	NV,
	NN,
	MVK,
	INTEL,
	IMG,
	HUAWEI,
	GOOGLE,
	GGP,
	FUCHSIA,
	ARM,
	ANDROID,
	AMD,
	EXT,
	KHR;

	companion object {
		fun get(string: String) = entries.firstOrNull { string.endsWith(it.name) }
		fun length(string: String) = get(string)?.name?.length ?: 0
		fun underscoredLength(string: String) = get(string)?.name?.length?.plus(1) ?: 0
		fun drop(string: String) = string.dropLast(length(string))
	}

}