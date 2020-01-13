(function($){
			function getPosition(idx){
				return {
					left: idx%3 * 150,
					top: parseInt(idx/3,10) * 150
				}
			}
			
			function findBlank(cells){
				for(var i = 0; i < cells.length; i++){
					if(cells[i] == 9){
						return i;
					}
				}
			}
			
			
			function drawCell(cells){
				for(var i=0; i<cells.length; i++){
					if(cells[i] != 9){
						var p = getPosition(i)
						console.log("#cell" + (cells[i]) + '=' + 'left:' + p.left + ' top:' + p.top)
						$("#cell" + (cells[i])).animate({
							left: ''+ p.left + 'px',
							top: '' + p.top + 'px'
						})
					}
				}
			}
			$(document).ready(function(){

				var xhr;
    			$('#loginBtn').click(function () {
        			$.ajax({
            			url:'/hello/login',
            			data:$('#login').serialize(),
            			success:function (data) {
                			if(data=="success"){
                    			alert("登陆成功");
                			}else{
                    			alert("登陆失败");
                			}
                			window.location.href="/hello";
            			}
        			})
    			})
				var index = 1;
				var da;
				
				$("#shuffle").click(function(){
					if(xhr != null)
					xhr.abort();
					$(".chess .cell").stop(true, false);
					var jqxhr = $.post( "/hello/shuffle")
					  .done(function(data) {
						
					    drawCell(data)
						console.log("data=", data)
						da = data;
						$(".chess").data("cells", data)
						index = 1;
						
					  })
					
				})
				var jqxhr2;
				
				$("#solve").click(function solving(){
					var cells = $(".chess").data("cells");
					//console.log("cells=",cells);
				/*$.post( "/hello/solve",'"[' + cells + ']"' , function( data ) {
                  jqxhr2 = data;
                }, "json")*/
			
						xhr = $.ajax({
			    			type: 'post',
			    			url: '/hello/solve',
			    			contentType: 'application/json;charset=utf-8',
			    			data: JSON.stringify($(".chess").data("cells")),
			    			success: function solvingChess(data) { //json结果
								if(index < data.length){
									jqxhr2 = data[index];
									var blankBefore = findBlank(data[index - 1])
									var blankAfter = findBlank(data[index])
									console.log("data=", data[index])
									var p1 = getPosition(blankBefore)
									var p2 = getPosition(blankAfter)
									console.log("position=" + p1.left +" " + p1.top)
									console.log("position=" + p2.left +" " + p2.top)
									console.log("cells", data[index - 1][blankAfter])
									$("#cell" + (data[index - 1][blankAfter])).animate({
										left: '' + p1.left + 'px',
										top: '' + p1.top + 'px'
									} ,600, solving);
									//drawCell(data[index])
									index++;
								}
								
								
								if(index == data.length && $(".chess .cell").is(":animated")){
									setTimeout(function(){
										alert("Solved");
										index = 1;
										data = da;
										drawCell(da);
										$(".chess").data("cells", data);
									}, 1200)
								}
								
			    			}
							});
				})
			})
		})(jQuery)