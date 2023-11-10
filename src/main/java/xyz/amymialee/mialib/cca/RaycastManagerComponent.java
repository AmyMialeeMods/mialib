package xyz.amymialee.mialib.cca;

//public class RaycastManagerComponent implements Component {
//	private static final Set<Identifier> RECORDS = new HashSet<>();
//
//	@Override
//	public void readFromNbt(@NotNull NbtCompound tag) {
//		for (var key : tag.getKeys()) {
//			var record = new RaycastRecord(Map.of());
//			var recordCompound = tag.getCompound(key);
//			for (var entryKey : recordCompound.getKeys()) {
//				record.targets.put(Entity.getUuidFrom(new NbtCompound()), recordCompound.getInt(entryKey));
//			}
//			records.add(record);
//		}
//	}
//
//	@Override
//	public void writeToNbt(NbtCompound tag) {
//		var compound = new NbtCompound();
//		for (var record : records) {
//			var recordCompound = new NbtCompound();
//			for (var entry : record.targets.entrySet()) {
//				recordCompound.putInt(entry.getKey().getUuid().toString(), entry.getValue());
//			}
//			compound.put(record.toString(), recordCompound);
//		}
//	}
//
//	public record RaycastRecord(Map<Entity, Integer> targets) {
//		public void tick() {
//			for (var entry : targets.entrySet()) {
//				entry.setValue(entry.getValue() - 1);
//				if (entry.getValue() <= 0) {
//					targets.remove(entry.getKey());
//				}
//			}
//		}
//
//		public void add(Entity entity, int ticks) {
//			targets.put(entity, ticks);
//			// TODO: sync removal
//		}
//
//		public void remove(Entity entity) {
//			targets.remove(entity);
//			// TODO: sync removal
//		}
//	}
//}
