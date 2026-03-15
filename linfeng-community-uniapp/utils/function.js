export default {

	// 公共跳转方法
	jump(url, type = 1) {
		// 保留当前页面，跳转到应用内的某个页面
		if (type == 1) {
			uni.navigateTo({
				url: url
			})
		}

		// 关闭当前页面，跳转到应用内的某个页面
		if (type == 2) {
			uni.redirectTo({
				url: url
			})
		}
		// 关闭所有页面，打开到应用内的某个页面
		if (type == 3) {
			uni.reLaunch({
				url: url
			})
		}

		// 跳转到 tabBar 页面，并关闭其他所有非 tabBar 页面
		if (type == 4) {
			uni.switchTab({
				url: url
			})
		}
	},
	tostring(value) {
		return JSON.stringify(value);
	},
	toast(title, icon = 'none') {
		uni.showToast({
			title: title,
			icon: icon,
			duration: 1500
		});
	},
	copy(res, isToast = true) {
		uni.setClipboardData({
			data: res,
			success: function() {
				if (isToast) {
					uni.showToast({
						title: "复制成功",
						duration: 2000,
						icon: "none",
					});
				}
			},
			fail() {
				console.log('copy fail')
			}
		});

	},
	myCache(key, value, seconds = 3600 * 24) {
		let nowTime = Date.parse(new Date()) / 1000;
		if (key && value) {
			let expire = nowTime + Number(seconds);
			uni.setStorageSync(key, JSON.stringify(value) + '|' + expire)
		} else if (key && !value) {
			let val = uni.getStorageSync(key);
			if (val) {
				let temp = val.split('|')
				if (!temp[1] || temp[1] <= nowTime) {
					uni.removeStorageSync(key)
					return '';
				} else {
					return JSON.parse(temp[0]);
				}
			}
		}
	},
	// 将文本中的网址样式修改为蓝色，并添加链接（H5版本，使用DOM API）
	formatText(text) {
		// 如果文本为空，直接返回
		if (!text) return text;
		
		// 创建一个临时DOM元素来解析HTML
		const tempDiv = document.createElement('div');
		tempDiv.innerHTML = text;
		
		// 获取所有文本节点
		const textNodes = [];
		const walk = document.createTreeWalker(
			tempDiv,
			NodeFilter.SHOW_TEXT,
			null,
			false
		);
		
		let node;
		while (node = walk.nextNode()) {
			textNodes.push(node);
		}
		
		// 处理每个文本节点中的URL
		textNodes.forEach(textNode => {
			const text = textNode.nodeValue;
			const urlRegex = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
			const urls = text.match(urlRegex);
			
			if (urls) {
				let lastIndex = 0;
				let result = '';
				
				urls.forEach(url => {
					// 找到URL在文本中的位置
					const index = text.indexOf(url, lastIndex);
					if (index !== -1) {
						// 添加URL前的文本
						result += text.substring(lastIndex, index);
						// 添加带链接的URL
						result += `<a href="${url}" style="color: #aaaaff;">${url}</a>`;
						lastIndex = index + url.length;
					}
				});
				
				// 添加剩余的文本
				result += text.substring(lastIndex);
				
				// 创建新的HTML元素替换文本节点
				const span = document.createElement('span');
				span.innerHTML = result;
				textNode.parentNode.replaceChild(span, textNode);
			}
		});
		
		return tempDiv.innerHTML;
	},
	// 将文本中的网址样式修改为蓝色，并添加链接（小程序和App版本，纯字符串处理）
	formatTextForMiniProgram(text) {
		// 如果文本为空，直接返回
		if (!text) return text;
		
		// URL正则表达式：匹配 http://, https://, ftp://, file:// 开头的URL
		const urlRegex = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/gi;
		
		// 找到所有<a>标签的位置（包括开始和结束标签），避免处理已经在链接中的URL
		const linkTagRegex = /<\/?a\s*[^>]*>/gi;
		const linkTags = [];
		let match;
		
		while ((match = linkTagRegex.exec(text)) !== null) {
			const tag = match[0];
			const isOpeningTag = tag.toLowerCase().startsWith('<a');
			const isClosingTag = tag.toLowerCase().startsWith('</a');
			
			linkTags.push({
				start: match.index,
				end: match.index + tag.length,
				tag: tag,
				isOpening: isOpeningTag,
				isClosing: isClosingTag
			});
		}
		
		// 如果没有<a>标签，直接处理整个文本
		if (linkTags.length === 0) {
			return text.replace(urlRegex, (url) => {
				return `<a href="${url}" style="color: #aaaaff;">${url}</a>`;
			});
		}
		
		// 计算哪些区域在<a>标签内（不应处理）
		const excludedRanges = [];
		let openTagStack = [];
		
		for (let i = 0; i < linkTags.length; i++) {
			const tag = linkTags[i];
			if (tag.isOpening) {
				openTagStack.push(tag.start);
			} else if (tag.isClosing && openTagStack.length > 0) {
				const start = openTagStack.pop();
				excludedRanges.push({
					start: start,
					end: tag.end
				});
			}
		}
		
		// 如果有未闭合的<a>标签，也排除到最后
		if (openTagStack.length > 0) {
			excludedRanges.push({
				start: openTagStack[0],
				end: text.length
			});
		}
		
		// 合并重叠的排除区域
		excludedRanges.sort((a, b) => a.start - b.start);
		const mergedRanges = [];
		for (let i = 0; i < excludedRanges.length; i++) {
			if (mergedRanges.length === 0 || excludedRanges[i].start > mergedRanges[mergedRanges.length - 1].end) {
				mergedRanges.push(excludedRanges[i]);
			} else {
				mergedRanges[mergedRanges.length - 1].end = Math.max(
					mergedRanges[mergedRanges.length - 1].end,
					excludedRanges[i].end
				);
			}
		}
		
		// 检查位置是否在排除区域内
		const isInExcludedRange = (pos) => {
			return mergedRanges.some(range => pos >= range.start && pos < range.end);
		};
		
		// 找到所有URL，但只处理不在<a>标签内的
		const parts = [];
		const urlMatches = [];
		
		// 找到所有URL的位置（需要重置正则表达式）
		urlRegex.lastIndex = 0;
		while ((match = urlRegex.exec(text)) !== null) {
			const urlStart = match.index;
			const urlEnd = match.index + match[0].length;
			
			// 检查URL是否完全在排除区域内
			let isExcluded = false;
			for (let i = 0; i < mergedRanges.length; i++) {
				const range = mergedRanges[i];
				if (urlStart >= range.start && urlEnd <= range.end) {
					isExcluded = true;
					break;
				}
			}
			
			if (!isExcluded) {
				urlMatches.push({
					start: urlStart,
					end: urlEnd,
					url: match[0]
				});
			}
		}
		
		// 按位置顺序处理URL
		let currentPos = 0;
		for (let i = 0; i < urlMatches.length; i++) {
			const urlMatch = urlMatches[i];
			
			// 添加URL前的文本
			if (urlMatch.start > currentPos) {
				parts.push(text.substring(currentPos, urlMatch.start));
			}
			
			// 添加处理后的URL
			parts.push(`<a href="${urlMatch.url}" style="color: #aaaaff;">${urlMatch.url}</a>`);
			currentPos = urlMatch.end;
		}
		
		// 添加剩余的文本
		if (currentPos < text.length) {
			parts.push(text.substring(currentPos));
		}
		
		return parts.join('');
	},
	//url路径解密函数
	decryptUrl(encryptedUrl) {
	  var decryptionKey = 'linfengcommunitySYKey'; // 与后端相同的解密密钥
	  // 自定义解密逻辑
	  // 这里使用与加密相反的操作，将密文的每个字符减去密钥的字符来还原明文
	  var decryptedUrl = '';
	  for (var i = 0; i < encryptedUrl.length; i++) {
	    var encryptedChar = encryptedUrl.charAt(i);
	    var decryptedChar = String.fromCharCode(encryptedChar.charCodeAt(0) - decryptionKey.charCodeAt(i % decryptionKey.length));
	    decryptedUrl += decryptedChar;
	  }
	  return decryptedUrl;
	},
	//判空
	isEmpty(value) {
		if (value === undefined || value === null) return true;
		if (typeof value === 'string') return value.trim() === '';
		if (Array.isArray(value)) return value.length === 0;
		if (typeof value === 'object') return Object.keys(value).length === 0;
		return false;
	},

}