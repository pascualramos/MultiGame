package mx.ecosur.multigame.manantiales.token
{
	import mx.ecosur.multigame.enum.manantiales.TokenType;
	import mx.ecosur.multigame.enum.Color;
	
	public class ViveroToken extends ManantialesToken
	{
		public function ViveroToken()
		{
			super();
			_tooltip = resourceManager.getString("StringsBundle", "manantiales.token.vivero");
			_label = "E";
			_type = TokenType.VIVERO;
		}
	}
}